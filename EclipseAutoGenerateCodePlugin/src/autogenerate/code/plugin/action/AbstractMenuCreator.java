package autogenerate.code.plugin.action;



import java.net.URL;
import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionDelegate;

/**
 * not used
 * @author wlh
 * @deprecated
 */
public abstract class AbstractMenuCreator {
    protected IStructuredSelection selection;

    public AbstractMenuCreator() {
    }

    public void selectionChanged(IAction action, ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            this.selection = (IStructuredSelection)selection;
        }

    }

    protected abstract void createMenu(Menu var1);

    protected IAction addMenu(String id, String text, IActionDelegate actionDelegate, Menu menu) {
        return this.addMenu(id, text, actionDelegate, menu, (String)null);
    }

    protected IAction addMenu(String id, String text, IActionDelegate actionDelegate, Menu menu, int style) {
        AbstractMenuCreator.ActionProxy action = new AbstractMenuCreator.ActionProxy(id, text, actionDelegate, style);
        return this.addMenu(action, menu, (String)null);
    }

    protected IAction addMenu(String id, String text, IActionDelegate actionDelegate, Menu menu, String image) {
        AbstractMenuCreator.ActionProxy action = new AbstractMenuCreator.ActionProxy(id, text, actionDelegate);
        return this.addMenu(action, menu, image);
    }

    private IAction addMenu(AbstractMenuCreator.ActionProxy action, Menu menu, String image) {
        if (image != null) {
            action.setImageDescriptor(getImageDescriptor(image));
        }

        ActionContributionItem item = new ActionContributionItem(action);
        item.fill(menu, -1);
        return action;
    }

    public ImageDescriptor getImageDescriptor(String key) {
        if (key != null && key.length() != 0) {
            try {
            	
            	URL BASE_URL = new URL("icons/");
                URL url = new URL(BASE_URL, key);
                ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(url);
                return imageDescriptor;
            } catch (Throwable var3) {
                return null;
            }
        } else {
            return null;
        }
    }
    
    class ActionProxy extends Action {
        private IActionDelegate action;

        public ActionProxy(String id, String text, IActionDelegate action) {
            super(text);
            this.action = action;
            this.setId(id);
        }

        public ActionProxy(String id, String text, IActionDelegate action, int style) {
            super(text, style);
            this.action = action;
            this.setId(id);
        }

        public void run() {
            this.action.selectionChanged(this, AbstractMenuCreator.this.selection);
            this.action.run(this);
        }
    }
}