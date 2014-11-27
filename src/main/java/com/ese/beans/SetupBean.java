package com.ese.beans;

import com.ese.model.db.MSItemModel;
import com.ese.model.db.MSLocationItemsModel;
import com.ese.model.db.MSLocationModel;
import com.ese.model.db.MSWarehouseModel;
import com.ese.model.view.LocationView;
import com.ese.model.view.SetupView;
import com.ese.model.view.WarehouseAndLocationView;
import com.ese.model.view.WarehouseView;
import com.ese.model.view.dilog.WarehouseDialogView;
import com.ese.service.*;
import com.ese.utils.FacesUtil;
import com.ese.utils.MessageDialog;
import com.ese.utils.Utils;
import com.ese.service.LocationService;
import com.ese.service.SetupService;
import com.ese.service.WarehouseService;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ViewScoped
@ManagedBean(name = "setup")
public class SetupBean extends Bean{
    @ManagedProperty("#{setupService}") private SetupService setupService;
    @ManagedProperty("#{locationService}") private LocationService locationService;
    @ManagedProperty("#{warehouseService}") private WarehouseService warehouseService;
    @ManagedProperty("#{locationItemService}") private LocationItemService locationItemService;
    @ManagedProperty("#{itemService}") private ItemService itemService;

    private SetupView setupView;
    private WarehouseAndLocationView warehouseAndLocationView;
    private WarehouseDialogView warehouseDialogView;
    private List<WarehouseAndLocationView> warehouseAndLocationViewList;
    private boolean flagBtnNewWarehouse;
    private boolean flagBtnAddShowItem;
    private boolean flagBtnAddEdit;
    private boolean flagBtnDelete;
    private String modeWarehouse;
    private String nameBtn;
    private List<LocationView> locationViewList;
    private LocationView locationView;
    private List<MSWarehouseModel> msWarehouseModelList;
    private MSWarehouseModel msWarehouseModel;
    private List<MSLocationModel> msLocationModelList;
    private MSLocationModel msLocationModel;
    private WarehouseView warehouseView;
    private List<MSLocationItemsModel> msLocationItemsModelList;
    private MSLocationItemsModel msLocationItemsModel;
    private String itemSearch;
    private List<MSItemModel> msItemModelList;
    private String selectType;
    private List<MSItemModel> selectItem;
    private List<MSLocationItemsModel> selectLocationItem;

    public SetupBean() {

    }

    @PostConstruct
    private void init(){
        preLoad();
        setupView = new SetupView();
        warehouseDialogView = new WarehouseDialogView();
        locationView = new LocationView();
        msWarehouseModel = new MSWarehouseModel();
        warehouseView = new WarehouseView();
        msLocationItemsModel = new MSLocationItemsModel();
        modeWarehouse = "Mode(New)";
        nameBtn = "Cancel";
        btnOnLoad();
        onLoadLocationTB();
        warehouseOnLoad();
    }

    private void btnOnLoad(){
        flagBtnNewWarehouse = false;
        flagBtnAddShowItem = true;
        flagBtnAddEdit = false;
        flagBtnDelete = true;
    }

    private void warehouseOnLoad(){
        msWarehouseModelList = warehouseService.getWarehouseAll();
    }

    private void onLoadLocationTB(){
        msLocationModelList = locationService.getLocationAll();
    }

    public void btnWarehouseAndLocation(String target){
        if (target.equalsIgnoreCase("SaveOrUpdate")){
            log.debug("onSaveOrUpdate() {}", locationView);
            try {
                locationService.onSaveOrUpdateLocationToDB(locationView);
                showDialogSaved();
                init();
            } catch (Exception e) {
                log.debug("Exception onSaveLocation : ", e);
                showDialogError(e.getMessage());
            }
        } else if (target.equalsIgnoreCase("Delect")){
            log.debug("-- onDelete()");
            try {
                locationService.delete(msLocationModel);
                showDialogDeleted();
                init();
            } catch (Exception e) {
                log.error("{}",e);
                showDialogError(e.getMessage());
            }
        } else if (target.equalsIgnoreCase("NewOrCancel")){
            log.debug("NewOrCancel.");
            locationView = new LocationView();
            btnOnLoad();
        }  else if (target.equalsIgnoreCase("ClickOnTable")){
            log.debug("onClickToLocationTB(), {}", msLocationModel.toString());
            modeWarehouse = "Mode(Edit)";
            nameBtn = "New";
            flagBtnDelete = false;
            flagBtnAddShowItem = false;
            locationView = locationService.clickToView(msLocationModel);
        }
    }

    public void warehouseDlg(String target){
        log.debug("onLoadWarehouseDlg().");

        if (target.equalsIgnoreCase("ADD")){
            log.debug("Open Warehouse Dialog.");
            warehouseView = new WarehouseView();
            msWarehouseModelList = warehouseService.getWarehouseAll();
        } else if (target.equalsIgnoreCase("New")){
            warehouseView = new WarehouseView();
        } else if (target.equalsIgnoreCase("Save")){
            log.debug("OnSave Warehouse. {}", warehouseView);
            warehouseService.onSaveOrUpdateWarehouse(warehouseView);
            showDialogSaved();
            init();
        } else if (target.equalsIgnoreCase("Delete")){
            log.debug("Delete warehouse.");
            warehouseService.delete(msWarehouseModel);
            showDialog(MessageDialog.SAVE.getMessageHeader(), MessageDialog.SAVE.getMessage());
            init();
        } else if (target.equalsIgnoreCase("ClickOnTable")){
            log.debug("onclickWarehouseTBDlg(). {}",msWarehouseModel.toString());
            warehouseView = warehouseService.converToView(msWarehouseModel);
        }
    }

    public void actionTolocationDialog(String target){
        log.debug("actionTolocationDialog().");

        if (target.equalsIgnoreCase("AddItem")){
            log.debug("locationItemDialog(). {}", msLocationModel.getId());

            selectType = "3";
            itemSearch = "";
            msItemModelList = new ArrayList<MSItemModel>();
            msLocationItemsModelList = locationItemService.findLocationItemByLocationId(msLocationModel.getId());
        } else if (target.equalsIgnoreCase("SearchItem")){
            log.debug("-- onSubmitSearch() {}, {}", selectType,itemSearch);

            if(!Utils.isZero(itemSearch.length())){
                msItemModelList = itemService.findByCondition(selectType, itemSearch);
                log.debug("msItemModelList Size : {}", msItemModelList.size());
            }
        } else if (target.equalsIgnoreCase("AddToLocation")){
            log.debug("addToLocationItem");
            locationItemService.addToLocationItemModel(selectItem, msLocationModel);
            showDialog(MessageDialog.SAVE.getMessageHeader(), MessageDialog.SAVE.getMessage());
            init();
        } else if (target.equalsIgnoreCase("Remove")){
            log.debug("remove(). {}", selectLocationItem.size());
            locationItemService.deleteLocationItemModel(selectLocationItem);
            actionTolocationDialog("AddItem");
        }
    }

//    public void locationItemDialog(){
//        log.debug("locationItemDialog(). {}", msLocationModel.getId());
//        selectType = "3";
//        itemSearch = "";
//        msItemModelList = new ArrayList<MSItemModel>();
//        msLocationItemsModelList = locationItemService.findLocationItemByLocationId(msLocationModel.getId());
//    }
//
//    public void onSubmitSearch(){
//        log.debug("-- onSubmitSearch() {}, {}", selectType,itemSearch);
//
//        if(!Utils.isZero(itemSearch.length())){
//            msItemModelList = itemService.findByCondition(selectType, itemSearch);
//            log.debug("msItemModelList Size : {}", msItemModelList.size());
//        }
//    }
//
//    public void addToLocationItem(){
//        log.debug("addToLocationItem");
//        locationItemService.addToLocationItemModel(selectItem, msLocationModel);
//        showDialog(MessageDialog.SAVE.getMessageHeader(), MessageDialog.SAVE.getMessage());
//        init();
//    }
//
//    public void remove(){
//        log.debug("remove(). {}", selectLocationItem.size());
//        locationItemService.deleteLocationItemModel(selectLocationItem);
//        locationItemDialog();
//    }
}
