package com.inventory.ui.components;

import com.inventory.ui.theme.Theme;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatePicker extends JPanel {
    private Date selectedDate;
    private JTextField txtDate;
    private JPopupMenu popup;
    private Calendar cal;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    // Calendar Popup elements
    private JPanel gridPanel;
    private JButton btnMonthYearTitle;
    
    // View modes for the calendar grid
    private enum ViewMode { DAYS, MONTHS, YEARS }
    private ViewMode currentMode = ViewMode.DAYS;
    private int yearRangeStart;

    public DatePicker(Date defaultDate) {
        this.selectedDate = defaultDate;
        this.cal = Calendar.getInstance();
        this.cal.setTime(selectedDate);

        setLayout(new BorderLayout());
        setOpaque(false);

        // Date Display textfield (Clickable to trigger DatePicker popup)
        txtDate = new JTextField(sdf.format(selectedDate));
        txtDate.setEditable(false);
        txtDate.setBackground(Theme.BG_DARK);
        txtDate.setForeground(Theme.FG_LIGHT);
        txtDate.setCaretColor(Theme.FG_LIGHT);
        txtDate.setHorizontalAlignment(JTextField.CENTER);
        txtDate.setPreferredSize(new Dimension(120, 35));
        txtDate.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(txtDate, BorderLayout.CENTER);

        // Setup Popup Menu for Calendar Selection
        popup = new JPopupMenu();
        popup.setBackground(Theme.BG_CARD);
        popup.setBorder(BorderFactory.createLineBorder(Theme.PRIMARY, 1));
        
        // Add calendar panel to popup
        popup.add(createCalendarPanel());

        // Mouse listeners to show popup when text field is clicked
        MouseAdapter showPopupAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (txtDate.isEnabled()) {
                    showCalendarPopup();
                }
            }
        };
        txtDate.addMouseListener(showPopupAdapter);
    }

    private void showCalendarPopup() {
        cal.setTime(selectedDate);
        currentMode = ViewMode.DAYS; // Always reset to days view on show
        updateCalendar();
        popup.show(this, 0, getHeight());
    }

    public Date getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(Date date) {
        this.selectedDate = date;
        txtDate.setText(sdf.format(selectedDate));
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        txtDate.setEnabled(enabled);
        if (enabled) {
            txtDate.setBackground(Theme.BG_DARK);
        } else {
            txtDate.setBackground(Theme.BG_SIDEBAR); // look disabled
        }
    }

    private JPanel createCalendarPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Theme.BG_CARD);
        container.setPreferredSize(new Dimension(250, 240));

        // Header Panel: Left button, Center title button, Right button
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JButton btnPrev = new JButton("<");
        btnPrev.setFont(new Font("Consolas", Font.BOLD, 12));
        btnPrev.setBackground(Theme.BG_DARK);
        btnPrev.setForeground(Theme.FG_LIGHT);
        btnPrev.setFocusPainted(false);
        btnPrev.addActionListener(e -> {
            if (currentMode == ViewMode.DAYS) {
                cal.add(Calendar.MONTH, -1);
            } else if (currentMode == ViewMode.MONTHS) {
                cal.add(Calendar.YEAR, -1);
            } else if (currentMode == ViewMode.YEARS) {
                yearRangeStart -= 12;
            }
            updateCalendar();
        });

        JButton btnNext = new JButton(">");
        btnNext.setFont(new Font("Consolas", Font.BOLD, 12));
        btnNext.setBackground(Theme.BG_DARK);
        btnNext.setForeground(Theme.FG_LIGHT);
        btnNext.setFocusPainted(false);
        btnNext.addActionListener(e -> {
            if (currentMode == ViewMode.DAYS) {
                cal.add(Calendar.MONTH, 1);
            } else if (currentMode == ViewMode.MONTHS) {
                cal.add(Calendar.YEAR, 1);
            } else if (currentMode == ViewMode.YEARS) {
                yearRangeStart += 12;
            }
            updateCalendar();
        });

        // Clickable header title to change view modes (zoom out to Month/Year selections)
        btnMonthYearTitle = new JButton();
        btnMonthYearTitle.setFont(Theme.FONT_HEADER);
        btnMonthYearTitle.setForeground(Theme.FG_LIGHT);
        btnMonthYearTitle.setBackground(Theme.BG_CARD);
        btnMonthYearTitle.setBorder(null);
        btnMonthYearTitle.setFocusPainted(false);
        btnMonthYearTitle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnMonthYearTitle.addActionListener(e -> {
            if (currentMode == ViewMode.DAYS) {
                currentMode = ViewMode.YEARS;
                yearRangeStart = cal.get(Calendar.YEAR) - 5;
            } else if (currentMode == ViewMode.YEARS) {
                currentMode = ViewMode.MONTHS;
            } else {
                currentMode = ViewMode.DAYS;
            }
            updateCalendar();
        });

        header.add(btnPrev, BorderLayout.WEST);
        header.add(btnMonthYearTitle, BorderLayout.CENTER);
        header.add(btnNext, BorderLayout.EAST);
        container.add(header, BorderLayout.NORTH);

        // Days/Months/Years Grid panel
        gridPanel = new JPanel();
        gridPanel.setOpaque(false);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        container.add(gridPanel, BorderLayout.CENTER);

        return container;
    }

    private void updateCalendar() {
        gridPanel.removeAll();

        if (currentMode == ViewMode.DAYS) {
            // Header title shows e.g., "Juni 2026"
            SimpleDateFormat myFormat = new SimpleDateFormat("MMMM yyyy");
            btnMonthYearTitle.setText(myFormat.format(cal.getTime()));

            gridPanel.setLayout(new GridLayout(7, 7, 2, 2));

            // Add Sun-Sat header labels
            String[] dayNames = {"Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab"};
            for (String dayName : dayNames) {
                JLabel lbl = new JLabel(dayName, SwingConstants.CENTER);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
                lbl.setForeground(Theme.PRIMARY);
                gridPanel.add(lbl);
            }

            // Days grid calculations
            Calendar tempCal = (Calendar) cal.clone();
            tempCal.set(Calendar.DAY_OF_MONTH, 1);
            int startDayOfWeek = tempCal.get(Calendar.DAY_OF_WEEK); // 1 = Sun, ..., 7 = Sat
            int maxDays = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH);

            // Leading empty cells
            for (int i = 1; i < startDayOfWeek; i++) {
                gridPanel.add(new JLabel(""));
            }

            Calendar todayCal = Calendar.getInstance();
            Calendar selectedCal = Calendar.getInstance();
            selectedCal.setTime(selectedDate);

            // Day buttons
            for (int day = 1; day <= maxDays; day++) {
                final int selectedDay = day;
                JButton btnDay = new JButton(String.valueOf(day));
                btnDay.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                btnDay.setFocusPainted(false);
                btnDay.setBorder(null);

                boolean isSelected = (tempCal.get(Calendar.YEAR) == selectedCal.get(Calendar.YEAR) &&
                                      tempCal.get(Calendar.MONTH) == selectedCal.get(Calendar.MONTH) &&
                                      day == selectedCal.get(Calendar.DAY_OF_MONTH));
                
                boolean isToday = (tempCal.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR) &&
                                   tempCal.get(Calendar.MONTH) == todayCal.get(Calendar.MONTH) &&
                                   day == todayCal.get(Calendar.DAY_OF_MONTH));

                if (isSelected) {
                    btnDay.setBackground(Theme.PRIMARY);
                    btnDay.setForeground(Color.WHITE);
                } else if (isToday) {
                    btnDay.setBackground(Theme.BG_DARK);
                    btnDay.setForeground(Theme.SUCCESS);
                    btnDay.setBorder(BorderFactory.createLineBorder(Theme.SUCCESS, 1));
                } else {
                    btnDay.setBackground(Theme.BG_DARK);
                    btnDay.setForeground(Theme.FG_LIGHT);
                }

                btnDay.addActionListener(e -> {
                    cal.set(Calendar.DAY_OF_MONTH, selectedDay);
                    setSelectedDate(cal.getTime());
                    popup.setVisible(false);
                });

                gridPanel.add(btnDay);
            }

            // Trailing empty cells
            int totalCells = (startDayOfWeek - 1) + maxDays;
            int remainingCells = 42 - totalCells; // 6 rows * 7 columns = 42 cells
            for (int i = 0; i < remainingCells; i++) {
                gridPanel.add(new JLabel(""));
            }

        } else if (currentMode == ViewMode.MONTHS) {
            // Header title shows year, e.g., "2026"
            btnMonthYearTitle.setText(String.valueOf(cal.get(Calendar.YEAR)));

            gridPanel.setLayout(new GridLayout(4, 3, 4, 4));

            String[] monthNames = {
                "Jan", "Feb", "Mar", "Apr", "Mei", "Jun",
                "Jul", "Ags", "Sep", "Okt", "Nov", "Des"
            };

            int activeMonth = cal.get(Calendar.MONTH);
            for (int m = 0; m < 12; m++) {
                final int selectedMonth = m;
                JButton btnMonth = new JButton(monthNames[m]);
                btnMonth.setFont(new Font("Segoe UI", Font.BOLD, 12));
                btnMonth.setFocusPainted(false);
                btnMonth.setBorder(null);

                if (selectedMonth == activeMonth) {
                    btnMonth.setBackground(Theme.PRIMARY);
                    btnMonth.setForeground(Color.WHITE);
                } else {
                    btnMonth.setBackground(Theme.BG_DARK);
                    btnMonth.setForeground(Theme.FG_LIGHT);
                }

                btnMonth.addActionListener(e -> {
                    cal.set(Calendar.MONTH, selectedMonth);
                    currentMode = ViewMode.DAYS; // Switch back to days view
                    updateCalendar();
                });

                gridPanel.add(btnMonth);
            }

        } else if (currentMode == ViewMode.YEARS) {
            // Header title shows year range, e.g., "2021 - 2032"
            btnMonthYearTitle.setText(yearRangeStart + " - " + (yearRangeStart + 11));

            gridPanel.setLayout(new GridLayout(4, 3, 4, 4));

            int activeYear = cal.get(Calendar.YEAR);
            for (int i = 0; i < 12; i++) {
                final int selectedYear = yearRangeStart + i;
                JButton btnYear = new JButton(String.valueOf(selectedYear));
                btnYear.setFont(new Font("Segoe UI", Font.BOLD, 12));
                btnYear.setFocusPainted(false);
                btnYear.setBorder(null);

                if (selectedYear == activeYear) {
                    btnYear.setBackground(Theme.PRIMARY);
                    btnYear.setForeground(Color.WHITE);
                } else {
                    btnYear.setBackground(Theme.BG_DARK);
                    btnYear.setForeground(Theme.FG_LIGHT);
                }

                btnYear.addActionListener(e -> {
                    cal.set(Calendar.YEAR, selectedYear);
                    currentMode = ViewMode.MONTHS; // Switch down to months selection next
                    updateCalendar();
                });

                gridPanel.add(btnYear);
            }
        }

        gridPanel.revalidate();
        gridPanel.repaint();
    }
}
