package com.ese.beans;

import com.ese.model.db.MSItemModel;
import com.ese.model.db.MSLocationModel;
import com.ese.model.db.MSWarehouseModel;
import com.ese.model.db.PickingOrderModel;
import com.ese.model.view.*;
import com.ese.service.PickingOrderService;
import com.ese.service.security.UserDetail;
import com.ese.utils.FacesUtil;
import com.ese.utils.MessageDialog;
import com.ese.utils.Utils;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ViewScoped
@ManagedBean(name = "pickingOrderShowItemBean")
public class PickingOrderShowItemBean extends Bean {
    private static final long serialVersionUID = 4112578334029874840L;
    @ManagedProperty("#{pickingOrderShowItemService}") private com.ese.service.PickingOrderShowItemService pickingOrderShowItemService;
    @ManagedProperty("#{pickingOrderService}") private PickingOrderService pickingOrderService;

    private PickingOrderModel pickingOrderModel;

    private boolean flagItem;
    private boolean flagFIFOReserved;
    private boolean flagPeriodReserved;
    private boolean flagManualReserved;
    private boolean flagPoil;
    private boolean flagShowStatus;
    private boolean flagPrint;

    private String startBatch;
    private String toBatch;
    private String resultReserve;

    private List<PickingOrderShowItemView> orderLineModelList;
    private List<PickingOrderShowItemView> selectPickingLine;
    private PickingOrderShowItemView itemView;

    private List<MSWarehouseModel> msWarehouseModelList;
    private int warehouseId;

    private List<MSLocationModel> msLocationModelList;
    private int locationId;

    private List<LocationQtyView> locationQtyBox;
    private int locationQtyId;

    private List<LocationQtyView> locationQtyViewList;
    private LocationQtyView selectLocationQtyView;
    private int rowIndex;

    private int reservedManualQty;
    private ItemQtyView itemQtyView;
    private int pickingLineId;

    //addItemDialog
    private boolean flagSearch;
    private boolean flagSelect;
    private ItemQtySearchView itemQtySearchView;
    private List<MSItemModel> itemQtyViewList;
    private MSItemModel selectItemQty;

    private UserDetail userDetail;

    //showItemStatusDialog
    private List<ShowItemStatusView> showItemStatusViewList;
    private ShowItemStatusView selectShowItemStatus;
    private boolean flagRemove;

    @PostConstruct
    private void init(){
        HttpSession session = FacesUtil.getSession(false);
        pickingOrderModel = (PickingOrderModel) session.getAttribute("pickingOrderId");

        if(preLoad()/* && isAuthorize(key)*/){
            userDetail = getUser();
        }

        btnOnload();
        onLoadTable();
        onNewObject();
    }

    private void btnOnload(){
        flagItem = false;
        flagFIFOReserved = true;
        flagPeriodReserved = true;
        flagManualReserved = true;
        flagPoil = true;
        flagShowStatus = true;
        flagPrint = true;
    }

    private void onLoadTable(){
        orderLineModelList = pickingOrderShowItemService.getPickingOrderLineByPickingOrderId(pickingOrderModel.getId());
    }

    private void onNewObject(){
        selectPickingLine = new ArrayList<PickingOrderShowItemView>();
        selectLocationQtyView = new LocationQtyView();
        itemView = new PickingOrderShowItemView();
    }

    public void onClose(){
        FacesUtil.redirect("/site/pickingOrder.xhtml");
    }

    public void onClickTable(){

        if (Utils.isSafetyList(selectPickingLine)){
            if (selectPickingLine.size() == 1){
                for (PickingOrderShowItemView view : selectPickingLine){
                    if (view.getReservedQty() != view.getQty()) {
                        if (view.getStatusID() < 3){
                            if (selectPickingLine.size() > 1){
                                flagItem = true;
                                flagManualReserved = true;
                            } else {
                                flagItem = false;
                                flagManualReserved = false;
                            }
                            flagFIFOReserved = false;
                            flagPeriodReserved = false;
                            flagPoil = false;
                            flagShowStatus = false;
                            flagPrint = false;
                        }
                    } else {
                        flagFIFOReserved = true;
                        flagPeriodReserved = true;
                        flagManualReserved = true;
                        flagPoil = false;
                        flagShowStatus = false;
                    }
//                String flagReserved = pickingOrderShowItemService.checkQty(view.getId());

//                if ("Success".equals(flagReserved)){

//                } else {
//                    if (view.getStatusID() < 3){
//                        if (selectPickingLine.size() > 1){
//                            flagItem = true;
//                        } else {
//                            flagItem = false;
//                        }
//                    }
//
//                    flagPrint = false;
//                    flagShowStatus = false;
//                    flagFIFOReserved = true;
//                    flagPeriodReserved = true;
//                    flagManualReserved = true;
//                }
                }
            } else {
                flagPoil = false;
                flagShowStatus = false;
                flagFIFOReserved = true;
                flagPeriodReserved = true;
                flagManualReserved = true;
            }
        } else {
            btnOnload();
        }
    }

    public void onClickFoil(){
        if (Utils.isSafetyList(selectPickingLine)){
            showDialog(MessageDialog.WARNING.getMessageHeader(), "Click Yes to unwrap or wrap.", "foilDlg");
        }
    }

    public void warpOnFoil(){
        for (PickingOrderShowItemView itemView : selectPickingLine){
            pickingOrderShowItemService.updateIsFoil(itemView.getId(), itemView.getFoil());
        }

        init();
    }

    public void FIFOReserved(){

        for (PickingOrderShowItemView view : selectPickingLine){
            if (pickingOrderShowItemService.checkReserve(view.getItem())){
                pickingOrderShowItemService.onReserved(view.getId(), "", "");
                pickingOrderShowItemService.setStatusPickingOrder(pickingOrderModel.getId());
                showDialog(MessageDialog.SAVE.getMessageHeader(), "Success.", "msgBoxSystemMessageDlg");
                init();
            } else {
                showDialog(MessageDialog.WARNING.getMessageHeader(), "no item in warehouse", "msgBoxSystemMessageDlg");
            }
        }
    }

    public void onClickPeriodReserve(){
        for (PickingOrderShowItemView view : selectPickingLine){
            if (pickingOrderShowItemService.checkReserve(view.getItem())){
                showDialog("", "", "periodDlg");
            } else {
                showDialog(MessageDialog.WARNING.getMessageHeader(), "no item in warehouse", "msgBoxSystemMessageDlg");
            }
        }
    }

    public void onClickManualReserve(){
        for (PickingOrderShowItemView view : selectPickingLine){
            if (!Utils.isZero(view.getReservedQty())){
                showDialog("", "", "periodDlg");
            } else {
                showDialog(MessageDialog.WARNING.getMessageHeader(), "no item in warehouse", "msgBoxSystemMessageDlg");
            }
        }
    }

    public void PeriodReserve(){
        String[] sBatch = startBatch.split("-");
        String[] tBatch = toBatch.split("-");

        String batchStart = sBatch[0] + sBatch[1];
        String batchTo = tBatch[0] + tBatch[1];

        if (Utils.parseInt(batchStart, 0) <= Utils.parseInt(batchTo, 0)){
            for (PickingOrderShowItemView view : selectPickingLine){
                pickingOrderShowItemService.onReserved(view.getId(), startBatch, toBatch);
                pickingOrderShowItemService.setStatusPickingOrder(pickingOrderModel.getId());
                showDialog(MessageDialog.SAVE.getMessageHeader(), "Success.", "msgBoxSystemMessageDlg");
                init();
            }
        } else {
            showDialog(MessageDialog.WARNING.getMessageHeader(), "StartBatch > ToBatch", "msgBoxSystemMessageDlg");
        }
    }

    public void preManualReserved(){

        warehouseId = 0;
        msLocationModelList = new ArrayList<MSLocationModel>();
        locationQtyBox = new ArrayList<LocationQtyView>();

        if (selectPickingLine.size() > 1){
            showDialog(MessageDialog.WARNING.getMessageHeader(), "กรุณา Reserved ทีละ 1", "msgBoxSystemMessageDlg");
        } else {
            onLoadManualReserved();
        }
    }

    private void onLoadManualReserved(){
        log.debug("selectPickingLine Size : {}", selectPickingLine.size());
        for (PickingOrderShowItemView view : selectPickingLine){
            if (pickingOrderShowItemService.checkReserve(view.getItem())){
                locationQtyViewList = pickingOrderShowItemService.onManualReserved(view.getId(), "", 0, 0);
                pickingLineId = view.getId();
                itemView = view;
                msWarehouseModelList = pickingOrderShowItemService.getWarehouseAll();
                showDialog("", "", "manualDlg");
                log.debug("msLocationModelList Size : {}", msWarehouseModelList.size());
            } else {
                showDialog(MessageDialog.WARNING.getMessageHeader(), "no item in warehouse", "msgBoxSystemMessageDlg");
            }

        }
    }

    public void onCloseManual(){
        pickingOrderShowItemService.closeManual(pickingLineId);
    }

    public void getLocationByWarehouseId(){
        msLocationModelList = pickingOrderShowItemService.getLocationByWarehouse(warehouseId);
        locationQtyBox = new ArrayList<LocationQtyView>();
    }

    public void getBtachByLocationName(){
        locationQtyBox = pickingOrderShowItemService.getBatchByLocation(locationId);
    }

    public void onSearchByManual(){
        locationQtyViewList = pickingOrderShowItemService.getLocationQtyBySearch(itemView.getItem(), warehouseId, locationId, locationQtyId);
    }

    public void ManualReserved(){
        log.debug("reservedManualQty [{}]", reservedManualQty);
        if (reservedManualQty > selectLocationQtyView.getAvailable()){
            log.debug("1");
            showDialog(MessageDialog.WARNING.getMessageHeader(), "Reserved Qty > Avaliable Qty.", "msgBoxSystemMessageDlg");
        }
        else if (Utils.isZero(reservedManualQty)){
            log.debug("2");
            showDialog(MessageDialog.WARNING.getMessageHeader(), "กรุณาใส่จำนวน Reserved Qty.", "msgBoxSystemMessageDlg");
        } else  if (reservedManualQty <= selectLocationQtyView.getAvailable()){
            log.debug("3");
            boolean flagMessage = pickingOrderShowItemService.saveManualReserved(selectLocationQtyView, reservedManualQty, itemView.getId());
            log.debug("flagMessage [{}]", flagMessage);
            if (flagMessage){
                log.debug("3.1");
                pickingOrderShowItemService.setStatusPickingOrder(pickingOrderModel.getId());
                showDialog(MessageDialog.SAVE.getMessageHeader(), "Success.", "msgBoxSystemMessageDlg");
                init();
            } else {
                log.debug("3.2");
                showDialog(MessageDialog.WARNING.getMessageHeader(), "can't  reserve", "msgBoxSystemMessageDlg");
            }
        }
        reservedManualQty = 0;
//        locationQtyViewList = pickingOrderShowItemService.onManualReserved(itemView.getId(), "", 0, 0);
//        locationQtyViewList = pickingOrderShowItemService.getLocationQtyBySearch(itemView.getItem(), warehouseId, locationId, locationQtyId);
//        orderLineModelList = pickingOrderShowItemService.getPickingOrderLineByPickingOrderId(pickingOrderModel.getId());
        onLoadTable();
//        onLoadManualReserved();
        log.debug("itemView [{}]", itemView.toString());
    }

    public void onAddEditItem(){
        log.debug("--onAddEditItem.");
        if (!Utils.isZero(selectPickingLine.size())){
            itemQtyView = new ItemQtyView();
            if (selectPickingLine.size() > 1){
                flagItem = true;
            } else {
                showDialog("Edit Order Qty.", "", "itemQtyDlg");
                for (PickingOrderShowItemView view : selectPickingLine){
                    itemQtyView.setPickLineId(view.getId());
                    itemQtyView.setItemCode(view.getItem());
                    itemQtyView.setItemDes(view.getDescription());
                    itemQtyView.setOrderQty(view.getQty());
                    itemQtyView.setReservedQty(view.getReservedQty());
                }
            }
        } else {
            showDialog("searchItemQtyDlg");
            itemQtyViewList = new ArrayList<MSItemModel>();
            itemQtySearchView = new ItemQtySearchView();
            flagSearch = false;
            flagSelect = true;
        }
    }

    public void onSaveItemQty(){
        log.debug("------ {}", itemQtyView.toString());

        if (Utils.isZero(itemQtyView.getPickLineId())){
            if (!Utils.isZero(itemQtyView.getOrderQty())){
                pickingOrderShowItemService.onSavePickingLine(pickingOrderModel, userDetail, itemQtyView);
                showDialog(MessageDialog.SAVE.getMessageHeader(), "Success.", "msgBoxSystemMessageDlg");
                init();
            } else {
                showDialog(MessageDialog.WARNING.getMessageHeader(), "กรุณาใส่ Order Qty.", "msgBoxSystemMessageDlg");
            }
        } else {
            log.debug("OrderQty : [{}] :::::: ReservedQty : [{}]", itemQtyView.getOrderQty(), itemQtyView.getReservedQty());
            if (itemQtyView.getReservedQty() <= itemQtyView.getOrderQty()){
                pickingOrderShowItemService.saveItemQty(itemQtyView.getPickLineId(), itemQtyView.getOrderQty());
                showDialog(MessageDialog.SAVE.getMessageHeader(), "Success.", "msgBoxSystemMessageDlg");
                init();
            } else {
                showDialog(MessageDialog.WARNING.getMessageHeader(), "Edit fail. This item was reserved. Please cancel the reserved qty first.", "msgBoxSystemMessageDlg");
            }
        }
    }

    public void onSearchItemQty(){
        itemQtyViewList = pickingOrderShowItemService.searchItemQty(itemQtySearchView);
        log.debug("itemQtyViewList Size : {}", itemQtyViewList.size());
    }

    public void onClickItemQty(){
        flagSelect = false;
    }

    public void onSelectItemQty(){
        itemQtyView = new ItemQtyView();
        itemQtyView.setItemCode(selectItemQty.getItemId());
        itemQtyView.setItemName(selectItemQty.getItemName());
        itemQtyView.setItemDes(selectItemQty.getDSGThaiItemDescription());
        itemQtyView.setOrderQty(0);
    }

    public void onShowItemStatus(){
        if (selectPickingLine.size() > 1){
            showDialog(MessageDialog.WARNING.getMessageHeader(), "กรุณาเลือก Picking Order Line ทีละ 1", "msgBoxSystemMessageDlg");
        } else {
            showDialog("showItemStatusDlg");
            flagRemove = true;
            onLoadShowItemStatus();
        }
    }

    private void onLoadShowItemStatus(){
        selectShowItemStatus = new ShowItemStatusView();
        for (PickingOrderShowItemView view : selectPickingLine){
            itemQtyView = new ItemQtyView();
            itemQtyView.setItemCode(view.getItem());
            itemQtyView.setItemName(view.getItemName());
            itemQtyView.setItemDes(view.getDescription());
            itemQtyView.setOrderQty(view.getOrderQty());
            itemQtyView.setReservedQty(view.getReservedQty());
            itemQtyView.setPickingQty(view.getQty());
            showItemStatusViewList = pickingOrderShowItemService.getReservedOrder(view.getId());
        }
    }

    public void removeShowItemStatus(){
        pickingOrderShowItemService.onRemove(selectShowItemStatus.getId(), itemQtyView.getItemCode());
        onLoadShowItemStatus();
    }

    public void onClickShowItemStatusTable(){
        flagRemove = false;
    }

    public void stikerWorkLoadReport(){
        pickingOrderService.getStikerWorkLoadReport(pickingOrderModel.getId(), userDetail);
    }

    public void confirmationPackingReport(){
        pickingOrderService.getConfirmationPackingReport(pickingOrderModel.getId(), userDetail);
    }

    public void onCloseShowStatusDialog(){
        init();
    }
}
