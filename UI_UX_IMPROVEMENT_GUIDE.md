# 🎨 UI/UX Improvement & Accessibility Guide

> **Priority**: HIGH (Third phase after database optimization)  
> **Estimated Time**: 12 hours  
> **Prerequisites**: Compilation working, database optimized

## 🎯 OBJECTIVES

Transform the rigid, non-accessible interface into a responsive, WCAG-compliant user experience:
- Make all windows resizable and responsive
- Implement WCAG AA accessibility standards
- Optimize component rendering for 60 FPS
- Add virtual scrolling for large datasets
- Implement proper keyboard navigation

## 🔍 CURRENT UI PROBLEMS

### Critical Issues
1. **Non-resizable Windows**: `setResizable(false)` and hardcoded dimensions
2. **Poor Accessibility**: No keyboard navigation, insufficient contrast ratios
3. **Performance Issues**: `TaskListCell.updateItem` taking 37ms per operation
4. **No Responsiveness**: Fixed layouts don't adapt to screen sizes
5. **Visual Inconsistency**: Hardcoded colors and styles

### Impact on Users
- Users with disabilities cannot use the application
- Poor performance on lower-end hardware
- Unusable on different screen sizes/resolutions
- Frustrating user experience overall

## 🏗️ RESPONSIVE WINDOW ARCHITECTURE

### 1. Base Window Framework

**File**: `src/com/components/ResponsiveWindow.java`

```java
package com.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.Logger;

/**
 * MANDATORY: All application windows must extend this class
 * Provides responsive behavior and accessibility features
 */
public abstract class ResponsiveWindow extends JFrame {
    private static final Logger logger = Logger.getLogger(ResponsiveWindow.class.getName());
    
    // Responsive breakpoints
    protected static final int MOBILE_WIDTH = 480;
    protected static final int TABLET_WIDTH = 768;
    protected static final int DESKTOP_WIDTH = 1024;
    protected static final int LARGE_DESKTOP_WIDTH = 1440;
    
    // Default dimensions
    protected final Dimension minSize;
    protected final Dimension preferredSize;
    protected final Dimension maxSize;
    
    // Layout state
    private LayoutSize currentLayoutSize = LayoutSize.DESKTOP;
    private boolean isInitialized = false;
    
    public enum LayoutSize {
        MOBILE, TABLET, DESKTOP, LARGE_DESKTOP
    }
    
    protected ResponsiveWindow(String title, Dimension minSize, Dimension preferredSize) {
        super(title);
        this.minSize = minSize;
        this.preferredSize = preferredSize;
        this.maxSize = new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
        
        initializeResponsiveWindow();
    }
    
    private void initializeResponsiveWindow() {
        // MANDATORY: Enable window decorations for accessibility
        setUndecorated(false);
        setResizable(true);
        
        // Set size constraints
        setMinimumSize(minSize);
        setPreferredSize(preferredSize);
        setMaximumSize(maxSize);
        
        // Center on screen
        setLocationRelativeTo(null);
        
        // MANDATORY: Proper shutdown handling
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleWindowClosing();
            }
        });
        
        // MANDATORY: Responsive behavior
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (isInitialized) {
                    handleResize(getSize());
                }
            }
        });
        
        // MANDATORY: Accessibility setup
        setupAccessibility();
        
        // Set initial size
        pack();
        setSize(preferredSize);
        
        isInitialized = true;
    }
    
    private void setupAccessibility() {
        // Keyboard navigation
        setFocusTraversalPolicyProvider(true);
        setFocusTraversalPolicy(new AccessibleFocusTraversalPolicy());
        
        // Screen reader support
        getAccessibleContext().setAccessibleName(getTitle());
        getAccessibleContext().setAccessibleDescription(
            "Main application window: " + getTitle()
        );
        
        // High contrast support
        if (AccessibilitySettings.isHighContrastEnabled()) {
            applyHighContrastTheme();
        }
    }
    
    /**
     * MANDATORY: Handle responsive layout changes
     * Override this method to implement responsive behavior
     */
    protected void handleResize(Dimension newSize) {
        LayoutSize newLayoutSize = determineLayoutSize(newSize.width);
        
        if (newLayoutSize != currentLayoutSize) {
            currentLayoutSize = newLayoutSize;
            onLayoutSizeChanged(newLayoutSize);
        }
        
        // Validate minimum size
        if (newSize.width < minSize.width || newSize.height < minSize.height) {
            SwingUtilities.invokeLater(() -> {
                setSize(Math.max(newSize.width, minSize.width),
                       Math.max(newSize.height, minSize.height));
            });
        }
    }
    
    private LayoutSize determineLayoutSize(int width) {
        if (width < MOBILE_WIDTH) return LayoutSize.MOBILE;
        if (width < TABLET_WIDTH) return LayoutSize.TABLET;  
        if (width < DESKTOP_WIDTH) return LayoutSize.DESKTOP;
        return LayoutSize.LARGE_DESKTOP;
    }
    
    /**
     * MANDATORY: Implement responsive layout changes
     * Called when window crosses responsive breakpoints
     */
    protected abstract void onLayoutSizeChanged(LayoutSize newSize);
    
    /**
     * MANDATORY: Implement proper cleanup
     * Called when window is closing
     */
    protected abstract void handleWindowClosing();
    
    // Utility methods for responsive design
    protected boolean isMobileLayout() { return currentLayoutSize == LayoutSize.MOBILE; }
    protected boolean isTabletLayout() { return currentLayoutSize == LayoutSize.TABLET; }
    protected boolean isDesktopLayout() { return currentLayoutSize == LayoutSize.DESKTOP; }
    protected boolean isLargeDesktopLayout() { return currentLayoutSize == LayoutSize.LARGE_DESKTOP; }
}
```

### 2. WCAG-Compliant Color System

**File**: `src/com/components/AccessibleColorScheme.java`

```java
package com.components;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * MANDATORY: WCAG AA compliant color scheme
 * All colors tested for 4.5:1 contrast ratio minimum
 */
public class AccessibleColorScheme {
    // Base colors - WCAG AA compliant
    public static final Color BACKGROUND = new Color(0x111827);      // #111827
    public static final Color SURFACE = new Color(0x1f2937);         // #1f2937  
    public static final Color SURFACE_ELEVATED = new Color(0x374151); // #374151
    
    // Primary colors with proper contrast
    public static final Color PRIMARY = new Color(0x60a5fa);         // #60a5fa (blue-400)
    public static final Color PRIMARY_HOVER = new Color(0x3b82f6);   // #3b82f6 (blue-500)
    public static final Color PRIMARY_PRESSED = new Color(0x2563eb); // #2563eb (blue-600)
    public static final Color PRIMARY_DISABLED = new Color(0x64748b); // #64748b (slate-500)
    
    // Text colors - WCAG AA compliant
    public static final Color TEXT_PRIMARY = new Color(0xf8fafc);    // #f8fafc (slate-50)
    public static final Color TEXT_SECONDARY = new Color(0x94a3b8);  // #94a3b8 (slate-400)
    public static final Color TEXT_DISABLED = new Color(0x64748b);   // #64748b (slate-500)
    public static final Color TEXT_ON_PRIMARY = new Color(0x1e293b); // #1e293b (slate-800)
    
    // Status colors - WCAG AA compliant
    public static final Color SUCCESS = new Color(0x22c55e);         // #22c55e (green-500)
    public static final Color SUCCESS_BG = new Color(0x15803d);      // #15803d (green-700)
    public static final Color WARNING = new Color(0xf59e0b);         // #f59e0b (amber-500)
    public static final Color WARNING_BG = new Color(0xd97706);      // #d97706 (amber-600)
    public static final Color ERROR = new Color(0xef4444);           // #ef4444 (red-500)
    public static final Color ERROR_BG = new Color(0xdc2626);        // #dc2626 (red-600)
    
    // Focus and interaction states
    public static final Color FOCUS_RING = new Color(0x60a5fa);      // #60a5fa (blue-400)
    public static final Color SELECTION = new Color(0x3b82f6, 50);   // Semi-transparent blue
    
    // High contrast alternatives (for accessibility)
    private static final Map<Color, Color> highContrastMap = new HashMap<>();
    
    static {
        // High contrast mappings for accessibility
        highContrastMap.put(TEXT_SECONDARY, TEXT_PRIMARY);
        highContrastMap.put(PRIMARY_DISABLED, PRIMARY);
        highContrastMap.put(TEXT_DISABLED, TEXT_SECONDARY);
    }
    
    /**
     * MANDATORY: Validate contrast ratio before using colors
     * @param foreground Foreground color
     * @param background Background color
     * @return true if contrast ratio >= 4.5:1 (WCAG AA)
     */
    public static boolean isValidContrast(Color foreground, Color background) {
        double ratio = calculateContrastRatio(foreground, background);
        return ratio >= 4.5;
    }
    
    /**
     * Calculate WCAG contrast ratio between two colors
     */
    public static double calculateContrastRatio(Color c1, Color c2) {
        double l1 = getRelativeLuminance(c1);
        double l2 = getRelativeLuminance(c2);
        
        double lighter = Math.max(l1, l2);
        double darker = Math.min(l1, l2);
        
        return (lighter + 0.05) / (darker + 0.05);
    }
    
    private static double getRelativeLuminance(Color color) {
        double r = linearize(color.getRed() / 255.0);
        double g = linearize(color.getGreen() / 255.0);
        double b = linearize(color.getBlue() / 255.0);
        
        return 0.2126 * r + 0.7152 * g + 0.0722 * b;
    }
    
    private static double linearize(double value) {
        if (value <= 0.03928) {
            return value / 12.92;
        } else {
            return Math.pow((value + 0.055) / 1.055, 2.4);
        }
    }
    
    /**
     * Get high contrast version of color if accessibility mode is enabled
     */
    public static Color getAccessibleColor(Color color) {
        if (AccessibilitySettings.isHighContrastEnabled()) {
            return highContrastMap.getOrDefault(color, color);
        }
        return color;
    }
}
```

### 3. High-Performance Task List Component

**File**: `src/com/components/VirtualTaskList.java`

```java
package com.components;

import com.implementation.Tarea;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * MANDATORY: Virtual scrolling for performance with large datasets
 * Renders only visible items, maintains 60 FPS
 */
public class VirtualTaskList extends JPanel implements Scrollable {
    private static final int ITEM_HEIGHT = 80;
    private static final int VISIBLE_BUFFER = 5; // Extra items to render
    
    private List<Tarea> tasks;
    private int totalItems = 0;
    private int visibleStart = 0;
    private int visibleEnd = 0;
    private final TaskRenderer taskRenderer;
    private final PerformanceMonitor performanceMonitor;
    
    // Mouse interaction
    private int hoveredIndex = -1;
    private int selectedIndex = -1;
    
    // Keyboard navigation
    private boolean keyboardNavigationEnabled = true;
    
    public VirtualTaskList() {
        this.taskRenderer = new TaskRenderer();
        this.performanceMonitor = new PerformanceMonitor();
        
        setOpaque(true);
        setBackground(AccessibleColorScheme.BACKGROUND);
        setFocusable(true);
        
        setupEventHandlers();
        setupAccessibility();
    }
    
    private void setupEventHandlers() {
        // Mouse interaction
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = getIndexAtPoint(e.getPoint());
                if (index >= 0 && index < totalItems) {
                    setSelectedIndex(index);
                    fireTaskSelected(tasks.get(index));
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                setHoveredIndex(-1);
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int index = getIndexAtPoint(e.getPoint());
                setHoveredIndex(index);
            }
        });
        
        // Keyboard navigation
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!keyboardNavigationEnabled) return;
                
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        navigateUp();
                        e.consume();
                        break;
                    case KeyEvent.VK_DOWN:
                        navigateDown();
                        e.consume();
                        break;
                    case KeyEvent.VK_ENTER:
                    case KeyEvent.VK_SPACE:
                        if (selectedIndex >= 0) {
                            fireTaskActivated(tasks.get(selectedIndex));
                        }
                        e.consume();
                        break;
                    case KeyEvent.VK_HOME:
                        setSelectedIndex(0);
                        e.consume();
                        break;
                    case KeyEvent.VK_END:
                        setSelectedIndex(totalItems - 1);
                        e.consume();
                        break;
                }
            }
        });
        
        // Focus management
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (selectedIndex == -1 && totalItems > 0) {
                    setSelectedIndex(0);
                }
                repaint();
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                repaint();
            }
        });
    }
    
    private void setupAccessibility() {
        // Screen reader support
        getAccessibleContext().setAccessibleName("Task List");
        getAccessibleContext().setAccessibleDescription(
            "List of tasks. Use arrow keys to navigate, Enter to select."
        );
        
        // Role for screen readers
        getAccessibleContext().setAccessibleRole(AccessibleRole.LIST);
    }
    
    /**
     * MANDATORY: Optimized painting - only render visible items
     */
    @Override
    protected void paintComponent(Graphics g) {
        performanceMonitor.monitorUIOperation("VirtualTaskList.paintComponent", () -> {
            super.paintComponent(g);
            
            if (tasks == null || tasks.isEmpty()) {
                paintEmptyState(g);
                return;
            }
            
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            Rectangle clipBounds = g2d.getClipBounds();
            updateVisibleRange(clipBounds);
            
            // Render only visible items
            for (int i = visibleStart; i <= visibleEnd && i < tasks.size(); i++) {
                Rectangle itemBounds = getItemBounds(i);
                if (clipBounds.intersects(itemBounds)) {
                    renderTask(g2d, tasks.get(i), i, itemBounds);
                }
            }
            
            // Render focus indicator
            if (hasFocus() && selectedIndex >= 0) {
                renderFocusIndicator(g2d, selectedIndex);
            }
            
            g2d.dispose();
        });
    }
    
    private void updateVisibleRange(Rectangle clipBounds) {
        visibleStart = Math.max(0, (clipBounds.y / ITEM_HEIGHT) - VISIBLE_BUFFER);
        visibleEnd = Math.min(totalItems - 1, 
            ((clipBounds.y + clipBounds.height) / ITEM_HEIGHT) + VISIBLE_BUFFER);
    }
    
    private void renderTask(Graphics2D g2d, Tarea task, int index, Rectangle bounds) {
        // Determine item state
        boolean isSelected = (index == selectedIndex);
        boolean isHovered = (index == hoveredIndex);
        boolean hasFocus = hasFocus();
        
        // Render background
        Color backgroundColor = getItemBackgroundColor(isSelected, isHovered, hasFocus);
        g2d.setColor(backgroundColor);
        g2d.fillRoundRect(bounds.x + 4, bounds.y + 2, bounds.width - 8, bounds.height - 4, 8, 8);
        
        // Render task content
        taskRenderer.render(g2d, task, bounds, isSelected, isHovered);
        
        // Render selection/focus indicator
        if (isSelected) {
            g2d.setColor(AccessibleColorScheme.FOCUS_RING);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(bounds.x + 4, bounds.y + 2, bounds.width - 8, bounds.height - 4, 8, 8);
        }
    }
    
    private Color getItemBackgroundColor(boolean selected, boolean hovered, boolean hasFocus) {
        if (selected && hasFocus) {
            return AccessibleColorScheme.PRIMARY;
        }
        if (selected) {
            return AccessibleColorScheme.SURFACE_ELEVATED;
        }
        if (hovered) {
            return AccessibleColorScheme.SURFACE;
        }
        return AccessibleColorScheme.BACKGROUND;
    }
    
    /**
     * Update task list - triggers repaint of visible area only
     */
    public void setTasks(List<Tarea> tasks) {
        this.tasks = tasks;
        this.totalItems = tasks != null ? tasks.size() : 0;
        
        // Adjust selection if needed
        if (selectedIndex >= totalItems) {
            selectedIndex = Math.max(0, totalItems - 1);
        }
        
        // Update preferred size for scrolling
        setPreferredSize(new Dimension(getWidth(), totalItems * ITEM_HEIGHT));
        revalidate();
        repaint();
    }
    
    // Scrollable interface implementation
    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return new Dimension(400, 10 * ITEM_HEIGHT); // Show 10 items by default
    }
    
    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return ITEM_HEIGHT; // Scroll one item at a time
    }
    
    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return visibleRect.height - (visibleRect.height % ITEM_HEIGHT); // Page scroll
    }
    
    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true; // Fill available width
    }
    
    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false; // Allow vertical scrolling
    }
    
    // Utility methods
    private int getIndexAtPoint(Point point) {
        return point.y / ITEM_HEIGHT;
    }
    
    private Rectangle getItemBounds(int index) {
        return new Rectangle(0, index * ITEM_HEIGHT, getWidth(), ITEM_HEIGHT);
    }
    
    private void setSelectedIndex(int index) {
        if (index != selectedIndex && index >= -1 && index < totalItems) {
            selectedIndex = index;
            
            // Ensure selected item is visible
            if (selectedIndex >= 0) {
                Rectangle itemBounds = getItemBounds(selectedIndex);
                scrollRectToVisible(itemBounds);
            }
            
            repaint();
            fireSelectionChanged();
        }
    }
    
    private void setHoveredIndex(int index) {
        if (index != hoveredIndex) {
            hoveredIndex = index;
            repaint();
        }
    }
    
    // Event firing methods
    private void fireTaskSelected(Tarea task) {
        // Implement event firing
    }
    
    private void fireTaskActivated(Tarea task) {
        // Implement event firing  
    }
    
    private void fireSelectionChanged() {
        // Implement event firing
    }
}
```

### 4. Responsive Login Window Implementation

**Update**: `src/com/login/login.java`

```java
// Replace the constructor and window setup in login.java with this:

public class login extends ResponsiveWindow {
    // Existing fields...
    
    public login() {
        super("StudyHabits - Inicio de Sesión", 
              new Dimension(800, 600),   // Minimum size
              new Dimension(1024, 768)); // Preferred size
        
        gestorRegistro = new GestorRegistro();
        initComponents();
        setupResponsiveLayout();
        setupKeyboardNavigation();
    }
    
    @Override
    protected void onLayoutSizeChanged(LayoutSize newSize) {
        SwingUtilities.invokeLater(() -> {
            switch (newSize) {
                case MOBILE:
                    // Stack elements vertically, hide secondary elements
                    setupMobileLayout();
                    break;
                case TABLET:
                    // Simplified two-column layout
                    setupTabletLayout();
                    break;
                case DESKTOP:
                    // Standard layout with all elements
                    setupDesktopLayout();
                    break;
                case LARGE_DESKTOP:
                    // Expanded layout with additional information
                    setupLargeDesktopLayout();
                    break;
            }
            revalidate();
            repaint();
        });
    }
    
    private void setupResponsiveLayout() {
        // Use BorderLayout as base for responsive behavior
        setLayout(new BorderLayout());
        
        // Create responsive panels
        JPanel headerPanel = createHeaderPanel();
        JPanel mainPanel = createMainPanel();
        JPanel footerPanel = createFooterPanel();
        
        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
        
        // Apply initial layout
        onLayoutSizeChanged(LayoutSize.DESKTOP);
    }
    
    private void setupMobileLayout() {
        // Hide logo, simplify form
        Logo.setVisible(false);
        TextLogo.setVisible(false);
        
        // Stack form elements
        FlipFrame.setLayout(new BoxLayout(FlipFrame, BoxLayout.Y_AXIS));
        
        // Increase touch targets
        EntrarBtnt.setPreferredSize(new Dimension(-1, 50));
        SendRest.setPreferredSize(new Dimension(-1, 50));
    }
    
    private void setupDesktopLayout() {
        // Show all elements
        Logo.setVisible(true);
        TextLogo.setVisible(true);
        
        // Restore card layout
        FlipFrame.setLayout(new CardLayout());
        
        // Standard button sizes
        EntrarBtnt.setPreferredSize(null);
        SendRest.setPreferredSize(null);
    }
    
    private void setupKeyboardNavigation() {
        // MANDATORY: Proper tab order
        setFocusTraversalPolicy(new ContainerOrderFocusTraversalPolicy() {
            @Override
            public Component getComponentAfter(Container container, Component component) {
                if (component == Username) return Pass;
                if (component == Pass) return EntrarBtnt;
                if (component == EntryUser) return EntryMail;
                if (component == EntryMail) return EntryPass;
                if (component == EntryPass) return SendRest;
                return super.getComponentAfter(container, component);
            }
        });
        
        // MANDATORY: Enter key handling
        setupEnterKeyHandling();
    }
    
    @Override
    protected void handleWindowClosing() {
        // Cleanup resources
        if (gestorRegistro != null) {
            gestorRegistro.cerrarConexion();
        }
        
        // Exit application
        System.exit(0);
    }
}
```

## 🚀 IMPLEMENTATION CHECKLIST

### Phase 1: Foundation (4 hours)
- [ ] Create `ResponsiveWindow` base class
- [ ] Implement `AccessibleColorScheme` with WCAG validation
- [ ] Update `login.java` to extend `ResponsiveWindow`
- [ ] Test window resize behavior

### Phase 2: Components (4 hours)  
- [ ] Create `VirtualTaskList` component
- [ ] Implement `TaskRenderer` for efficient drawing
- [ ] Add keyboard navigation support
- [ ] Test with large datasets (1000+ items)

### Phase 3: Accessibility (2 hours)
- [ ] Add screen reader support
- [ ] Implement high contrast mode
- [ ] Test keyboard-only navigation
- [ ] Validate WCAG compliance

### Phase 4: Integration (2 hours)
- [ ] Update all windows to use responsive framework
- [ ] Test on different screen sizes
- [ ] Performance test with monitoring
- [ ] User acceptance testing

## 🎯 PERFORMANCE TARGETS

- **Frame Rate**: Maintain 60 FPS during scrolling
- **Paint Time**: < 16ms per frame
- **Resize Responsiveness**: < 100ms to adapt layout
- **Large Dataset**: Smooth scrolling with 10,000+ items
- **Memory Usage**: < 100MB for UI components

## 🔍 ACCESSIBILITY VALIDATION

Test with these tools:
```bash
# Screen reader testing
narrator.exe  # Windows Narrator

# Keyboard navigation testing
# Tab through all interactive elements
# Arrow keys for list navigation
# Enter/Space for activation

# Color contrast testing
# Use online contrast checkers
# Test in high contrast mode
```

## 🚫 UI ANTI-PATTERNS TO AVOID

```java
// ❌ NEVER: Hardcoded sizes
setSize(800, 600);

// ❌ NEVER: Disable focus
component.setFocusable(false);

// ❌ NEVER: Block EDT with long operations
SwingUtilities.invokeLater(() -> {
    Thread.sleep(1000); // Blocks UI!
});

// ❌ NEVER: Poor contrast
setForeground(Color.GRAY);
setBackground(Color.LIGHT_GRAY); // Insufficient contrast

// ❌ NEVER: No keyboard support
addMouseListener(onlyMouseListener); // Excludes keyboard users
```

---

*Complete this guide to ensure your application is accessible to all users and performs smoothly on all devices.*
