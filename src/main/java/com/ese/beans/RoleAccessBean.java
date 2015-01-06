package com.ese.beans;

import com.ese.model.db.MenuObjectModel;
import com.ese.model.db.RoleAccessModel;
import com.ese.model.db.SystemRoleModel;
import com.ese.model.view.SystemRoleView;
import com.ese.service.RoleAccessService;
import com.ese.utils.MessageDialog;
import com.ese.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.CheckboxTreeNode;
import org.primefaces.model.TreeNode;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ManagedBean(name = "roleAccess")
@ViewScoped
public class RoleAccessBean extends Bean{
    private static final long serialVersionUID = 4412578634029874840L;
    @ManagedProperty("#{roleAccessService}") private RoleAccessService roleAccessService;

    private String modeRole;
    private String modeRoleAccess;
    private boolean flagBtnSaveRole;
    private boolean flagBtnDeleteRole;
    private boolean flagBtnPrint;
    private String keySearchRole;
    private boolean flagBtnAddRoleAccess;
    private boolean flagBtnDeleteRoleAccess;
    private boolean flagItemRoleAccess;
    private boolean flagSearchRoleAccess;
    private boolean flagBtnSearchRoleAccess;
    private String keySearchRoleAccess;

    private List<SystemRoleModel> systemRoleModelList;
    private SystemRoleModel systemRoleModel;
    private List<RoleAccessModel> roleAccessModelList;
    private List<RoleAccessModel> selectRoleAccess;

    //Root RoleAccessMode
    private TreeNode rootRoleAccessMode;
    //Select Root
    private TreeNode[] selectRootRoleAccess;

    Map<Integer, CheckboxTreeNode> treeNodeMap;

    private SystemRoleView systemRoleView;
    private List<MenuObjectModel> menuObjectModelList;
    private MenuObjectModel menuObjectModel;

    //Add Role Access Dialog
    //TB
    private List<MenuObjectModel> menuRoleAccessDlgList;
    private List<MenuObjectModel> selectMenuRoleAccessDlg;
    private String keySearchMenuObj;

    //Item
    private List<MenuObjectModel> menuRoleAccessItemList;
    private MenuObjectModel menuRoleAccessItem;

    @PostConstruct
    private void onLoad(){
        btnAndtextOnload();
        newObjectOnload();
        roleTBOnload();
    }

    private void btnAndtextOnload(){
        modeRole = "Mode:Search";
        modeRoleAccess = "Mode:Search";
//        flagBtnSaveRole = true;
        flagBtnDeleteRole = true;
        flagBtnPrint = true;
        flagBtnAddRoleAccess = true;
        flagBtnDeleteRoleAccess = true;
        flagItemRoleAccess = true;
        flagSearchRoleAccess = true;
        flagBtnSearchRoleAccess = true;
        keySearchRole = "";
        keySearchRoleAccess = "";
        keySearchMenuObj = "";
    }

    private void newObjectOnload(){
        systemRoleModel = new SystemRoleModel();
        systemRoleView = new SystemRoleView();
        menuObjectModel = new MenuObjectModel();
        rootRoleAccessMode = new CheckboxTreeNode();
    }

    private void roleTBOnload(){
        systemRoleModelList = roleAccessService.getSystemRoleByIsValid();
        menuObjectModelList = roleAccessService.getMenuObjectByObjCategory();
    }

    public void onClickRoleTB(){
        log.debug("systemRoleModel Id : {}", systemRoleModel.getId());
        modeRole = "Mode:Edit";
//        flagBtnSaveRole = false;
        flagBtnDeleteRole = false;
        flagBtnPrint = false;
        flagBtnAddRoleAccess = false;
        flagBtnDeleteRoleAccess = false;
        flagItemRoleAccess = false;
        flagSearchRoleAccess = false;
        flagBtnSearchRoleAccess = false;
        systemRoleView = roleAccessService.getModelToView(systemRoleModel);
        roleAccessModelList = roleAccessService.getRoleAccessBySystemRoleId(systemRoleModel.getId());
        rootRoleAccessMode = creRootRoleAccess();
    }

    private TreeNode creRootRoleAccess() {
        TreeNode root = new CheckboxTreeNode(new Document("", ""), null);
        log.debug("roleAccessModelList Size : ", roleAccessModelList.size());
        final int MENU = 1;
        final int TAB = 2;
        final int ACTION = 3;

        treeNodeMap = new HashMap<Integer, CheckboxTreeNode>();
        for (RoleAccessModel model : roleAccessModelList) {
            if ( !Utils.isNull(model) ) {
                if ( !Utils.isZero(Utils.parseInt(model.getMenuObjectModel().getCode(), 0)) && model.getMenuObjectModel().getObjCategory() == MENU ) {
                    treeNodeMap.put(model.getMenuObjectModel().getId(), new CheckboxTreeNode(new Document( model.getMenuObjectModel().getCode(), model.getMenuObjectModel().getName()), root));
                } else if ( model.getMenuObjectModel().getObjCategory() == TAB ) {
                    treeNodeMap.put(model.getMenuObjectModel().getId(), new CheckboxTreeNode(new Document(model.getMenuObjectModel().getCode(), model.getMenuObjectModel().getName()), treeNodeMap.get(model.getMenuObjectModel().getParentId())));
                } else if ( model.getMenuObjectModel().getObjCategory() == ACTION ) {
                    new CheckboxTreeNode(new Document(model.getMenuObjectModel().getCode(), model.getMenuObjectModel().getName()), treeNodeMap.get(model.getMenuObjectModel().getParentId()));
                }
            }
        }

        return root;
    }

    public void onFilterRoleTB(){
        systemRoleModelList = roleAccessService.getSystemRoleByKey(keySearchRole);
        clearTextBox();

    }

    public void clearTextBox(){
        modeRole = "Mode:New";
        modeRoleAccess = "Mode:Search";
//        flagBtnSaveRole = true;
        flagBtnDeleteRole = true;
        flagBtnPrint = true;
        flagBtnAddRoleAccess = true;
        flagBtnDeleteRoleAccess = true;
        flagItemRoleAccess = true;
        flagSearchRoleAccess = true;
        flagBtnSearchRoleAccess = true;
        keySearchRole = "";
        systemRoleModel = new SystemRoleModel();
        systemRoleView = new SystemRoleView();
        roleAccessModelList = new ArrayList<RoleAccessModel>();
        rootRoleAccessMode = creRootRoleAccess();
    }

    public void preDeleteRole(){
        showDialog(MessageDialog.WARNING.getMessageHeader(), "Click Yes confirm delete.", "confirmDeleteRoleDlg");
    }

    public void onSaveRole(){
        roleAccessService.saveSystemRole(systemRoleView);
        showDialogSaved();
        onLoad();
        modeRole = "Mode:New";
        modeRoleAccess = "Mode:Search";
        roleAccessModelList = new ArrayList<RoleAccessModel>();
        rootRoleAccessMode = creRootRoleAccess();
    }

    public void onDeleteRole(){
        roleAccessService.deleteRole(systemRoleModel);
        showDialogDeleted();
        onLoad();
    }

    public void onFilterRoleAccess(){
        log.debug("Object ID : {}, SystemRoleId : {}, keySearchRoleAccess : {}", menuObjectModel.getId(), systemRoleModel.getId(), keySearchRoleAccess);
        roleAccessModelList = roleAccessService.getRoleAccessByMenuObjectIdAndSystemRoleId(menuObjectModel.getId(), systemRoleModel.getId(), keySearchRoleAccess);
        rootRoleAccessMode = creRootRoleAccess();
    }

    public void preDeleteRoelAccess(){
        if (!Utils.isSafetyList(selectRoleAccess)){
            showDialog(MessageDialog.WARNING.getMessageHeader(), "Please select Menu Object and Action.", "msgBoxSystemMessageDlg");
        } else {
            showDialog(MessageDialog.WARNING.getMessageHeader(), "Click Yes confirm delete.", "confirmDeleteRoleAccessDlg");
        }
    }

    public void onDeleteRoleAccess(){
        log.debug("selectRoleAccess Size : {}", selectRoleAccess.size());
        roleAccessService.deleteRoleAccess(selectRoleAccess);
        roleAccessModelList = roleAccessService.getRoleAccessBySystemRoleId(systemRoleModel.getId());
        rootRoleAccessMode = creRootRoleAccess();
    }

    public void onLoadMenuObjDialog(){
        selectMenuRoleAccessDlg = new ArrayList<MenuObjectModel>();
        menuRoleAccessItem = new MenuObjectModel();
        menuRoleAccessDlgList = roleAccessService.getMenuObjAll();
        menuRoleAccessItemList = roleAccessService.getMenuObjectByObjCategory();
    }

    public void onFilterMenuObjDlg(){
        menuRoleAccessDlgList = roleAccessService.getMenuObjByIdAndKey(menuRoleAccessItem.getId(), keySearchMenuObj);
    }

    public void onSaveMenuObjToRoleAccess(){
        if (Utils.isZero(selectMenuRoleAccessDlg.size())){
            showDialog(MessageDialog.WARNING.getMessageHeader(), "Please select Menu Object and Action.", "msgBoxSystemMessageDlg");
        } else {
            roleAccessService.saveMenuObjectInRoleAccess(selectMenuRoleAccessDlg, systemRoleModel);
            showDialogSaved();
            onLoadMenuObjDialog();
            roleAccessModelList = roleAccessService.getRoleAccessBySystemRoleId(systemRoleModel.getId());
            rootRoleAccessMode = creRootRoleAccess();
        }
    }
}
