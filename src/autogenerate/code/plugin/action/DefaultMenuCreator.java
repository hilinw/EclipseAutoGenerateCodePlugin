package autogenerate.code.plugin.action;

import autogenerate.code.plugin.tools.SelectionUtil;

import org.eclipse.swt.widgets.Menu;

/**
 * not used
 * @author wlh
 * @deprecated
 */

public class DefaultMenuCreator extends AbstractMenuCreator {
    public DefaultMenuCreator() {
    }

    protected void createMenu(Menu menu) {
        int selectionType = SelectionUtil.getSelectionType(this.selection);
        if (selectionType != 0) {
//            this.addMenu("AddFieldAction", "�ع�-����ֶ�", new AddFieldAction(), menu);
//            this.addMenu("RemoveFieldAction", "�ع�-ɾ���ֶ�", new RemoveFieldAction(), menu);
            this.addMenu("metadataCodeGenAction", "��������", new MetadataCodeGenAction(), menu);
//            this.addMenu("webCodeGenAction", "Web���������", new WebCodeGenAction(), menu);
 

        }
    }


}