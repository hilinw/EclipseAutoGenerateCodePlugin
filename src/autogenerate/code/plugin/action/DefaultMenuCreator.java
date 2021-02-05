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
//            this.addMenu("AddFieldAction", "重构-添加字段", new AddFieldAction(), menu);
//            this.addMenu("RemoveFieldAction", "重构-删除字段", new RemoveFieldAction(), menu);
            this.addMenu("metadataCodeGenAction", "代码生成", new MetadataCodeGenAction(), menu);
//            this.addMenu("webCodeGenAction", "Web层代码生成", new WebCodeGenAction(), menu);
 

        }
    }


}