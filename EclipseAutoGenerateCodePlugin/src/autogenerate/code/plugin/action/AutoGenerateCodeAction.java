package autogenerate.code.plugin.action;


import java.util.ArrayList;
import java.util.Iterator;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * not used
 * 
 * @author wlh
 * @deprecated
 */
public class AutoGenerateCodeAction implements IObjectActionDelegate, IMenuCreator {
    boolean fillMenu;
    IStructuredSelection selection;
    IAction delegateAction;
    static ArrayList<AbstractMenuCreator> creators = new ArrayList();

    static {
        creators.add(new DefaultMenuCreator());
    }

    public AutoGenerateCodeAction() {
    }

    public static void addCreator(AbstractMenuCreator creator) {
        creators.add(creator);
    }

    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
    }

    public void run(IAction action) {
    }

    public void selectionChanged(IAction action, ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            this.selection = (IStructuredSelection)selection;
            this.fillMenu = true;
            if (this.delegateAction != action) {
                this.delegateAction = action;
                this.delegateAction.setMenuCreator(this);
            }

            action.setEnabled(!selection.isEmpty());
            Iterator it = creators.iterator();

            while(it.hasNext()) {
                AbstractMenuCreator creator = (AbstractMenuCreator)it.next();
                creator.selectionChanged(action, selection);
            }
        }

    }

    public void dispose() {
    }

    public Menu getMenu(Control parent) {
        return new Menu(parent);
    }

    public Menu getMenu(Menu parent) {
        Menu menu = new Menu(parent);
        (new GroupMarker("new")).fill(menu, -1);
        (new GroupMarker("additions")).fill(menu, -1);
        menu.addMenuListener(new MenuAdapter() {
            public void menuShown(MenuEvent e) {
                if (AutoGenerateCodeAction.this.fillMenu) {
                    Menu m = (Menu)e.widget;
                    MenuItem[] items = m.getItems();

                    for(int i = 0; i < items.length; ++i) {
                        items[i].dispose();
                    }

                    Iterator it = AutoGenerateCodeAction.creators.iterator();

                    while(it.hasNext()) {
                        AbstractMenuCreator creator = (AbstractMenuCreator)it.next();
                        creator.createMenu(m);
                    }

                    AutoGenerateCodeAction.this.fillMenu = false;
                }

            }
        });
        return menu;
    }
}