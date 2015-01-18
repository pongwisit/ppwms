package com.ese.transform;

import com.ese.model.db.AXCustomerTableModel;
import com.ese.model.db.PickingOrderModel;
import com.ese.model.db.StatusModel;
import com.ese.model.view.DataSyncConfirmOrderView;
import com.ese.service.security.UserDetail;
import com.ese.utils.Utils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PickingOrderTransform extends Transform{

    public PickingOrderModel tranformToModel(DataSyncConfirmOrderView syncView, AXCustomerTableModel axCustomerTableModel, StatusModel statusModel, UserDetail detail){
        PickingOrderModel model = new PickingOrderModel();

//        model.setId(1);
        model.setDocNo(Utils.getDocumentDomesticOrder());
        model.setDocDate(Utils.currentDate());
        model.setCustomerCode(axCustomerTableModel);
        model.setConfirmId(syncView.getConfirmId());
        model.setConfirmDate(syncView.getConfirmDate());
        model.setSalesOrder(syncView.getSaleId());
        model.setPurchaseOrder(syncView.getPurchaseOrder());
        model.setDeliveryName(syncView.getDeliveryName());
        model.setDeliveryAddress(syncView.getDeliveryAddress());
        model.setCustomerRef(syncView.getCustomerRef());
        model.setContainers(syncView.getContainer());
        model.setPaymentCondition(syncView.getPaymentCondition());
        model.setDeliveryTerm(syncView.getDlvTerm());
        model.setRemark(syncView.getRemark());
        model.setSalesAdmin(syncView.getSaleAdmin());
        model.setModeDelivery(syncView.getModeDlv());
        model.setQuotation(syncView.getQuotationId());
        model.setQuotationDate(syncView.getQuotationDate());
        model.setStatus(statusModel);
        model.setIsValid(1);
        model.setVersion(1);
        model.setCreateBy(detail.getId());
        model.setCreateDate(Utils.currentDate());
        model.setUpdateBy(detail.getId());
        model.setUpdateDate(Utils.currentDate());

        model.setRequestShiftDate(Utils.currentDate()); //ยังไม่มีข้อมูลว่าดึงจากไหน
        model.setEddDate(Utils.currentDate());      //ยังไม่มีข้อมูลว่าดึงจากไหน
        model.setAvalibleDate(Utils.currentDate());      //ยังไม่มีข้อมูลว่าดึงจากไหน

        return model;
    }
}
