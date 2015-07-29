package com.ese.service;

import com.ese.model.dao.*;
import com.ese.model.db.*;
import com.ese.model.view.IncomingView;
import com.ese.model.view.StockMovementInView;
import com.ese.utils.AttributeName;
import com.ese.utils.FacesUtil;
import com.ese.utils.Utils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Component
@Transactional
public class IncomingService extends Service {
    private static final long serialVersionUID = 4112578634029876045L;
    @Resource private StockInOutDAO stockInOutDAO;
    @Resource private StockInOutNoteDAO stockInOutNoteDAO;
    @Resource private InvOnHandDAO invOnHandDAO;
    @Resource private PalletDAO palletDAO;
    @Resource private StockMovementInDAO stockMovementInDAO;
    @Resource private StockMovementOutDAO stockMovementOutDAO;

    public List<StockMovementInView> getStockMoveInByStockInOutId(int stockInoutId){
        return  stockMovementInDAO.findstockMovementOutByStockInOutId(stockInoutId);
    }

    public List<MSStockInOutNoteModel> getAllStockInOutNote(){
        return stockInOutNoteDAO.getStockInOutNoteOrderByTypeI();
    }

    public List<StockInOutModel> getOnLoad(){
        return stockInOutDAO.findByDocNoINAndCurrentDate();
    }

    public List<StockInOutModel> search(IncomingView incomingView){
        return stockInOutDAO.findBySearchIN(incomingView);
    }

    public void save(IncomingView incomingView){
        try {
            int staffModel = (int) FacesUtil.getSession(false).getAttribute(AttributeName.STAFF.getName());
            StockInOutModel stockInOutModel = new StockInOutModel();
            stockInOutModel.setDocNo(incomingView.getDocNo());
            stockInOutModel.setDocDate(incomingView.getDocDate());
            stockInOutModel.setMsStockInOutNoteModel(incomingView.getMsStockInOutNoteModel());
            stockInOutModel.setRemark(incomingView.getRemark());
            stockInOutModel.setUpdateDate(Utils.currentDate());
            stockInOutModel.setUpdateBy(staffModel);
            stockInOutModel.setCreateDate(Utils.currentDate());
            stockInOutModel.setCreateBy(staffModel);
            stockInOutModel.setIsValid(1);
            StatusModel statusModel = new StatusModel();
            statusModel.setId(17);
            stockInOutModel.setStatus(statusModel);
            stockInOutDAO.persist(stockInOutModel);
        } catch (Exception e) {
            log.debug("Exception error during save ", e);
        }
    }

    public void edit(IncomingView incomingView){
        try {
            int staffModel = (int) FacesUtil.getSession(false).getAttribute(AttributeName.STAFF.getName());
            StockInOutModel stockInOutModel = stockInOutDAO.findByID(incomingView.getId());
            stockInOutModel.setDocDate(incomingView.getDocDate());
            stockInOutModel.setMsStockInOutNoteModel(incomingView.getMsStockInOutNoteModel());
            stockInOutModel.setRemark(incomingView.getRemark());
            stockInOutModel.setUpdateDate(Utils.currentDate());
            stockInOutModel.setUpdateBy(staffModel);
            stockInOutDAO.update(stockInOutModel);
        } catch (Exception e) {
            log.debug("Exception error during edit ", e);
        }
    }

    public List<InvOnHandModel> findInvOnHand(String barcode){
        List<InvOnHandModel> invOnHandModelList = Utils.getEmptyList();
        try {
            log.debug("All {}", palletDAO.findAll().size());
            log.debug("-------- {}", palletDAO.findAll().toString());
            //pallet barcode PL or SN T
            //
            log.debug("findInvOnHand(barcode : {})", barcode);
            if (barcode.contains("PL")) {
                log.debug("PL");
                List<PalletModel> palletModelList = palletDAO.findByLikePalletBarcode(barcode);
                log.debug("palletModelList Size: {}", palletModelList.size());
                for (PalletModel model : palletModelList) {
                    invOnHandModelList = invOnHandDAO.findByPalletId(model.getId());
                }
            } else if (barcode.contains("T")) {
                log.debug("T");
                log.debug("----- {}", invOnHandDAO.findAll().toString());
                invOnHandModelList = invOnHandDAO.findByLikeSnBarcode(barcode);
            }
            log.debug("invOnHandModelList.size().[{}}", invOnHandModelList.size());
//            if ("PL".equalsIgnoreCase(barcode)) {
//                List<PalletModel> palletModelList = palletDAO.findByLikePalletBarcode(barcode);
//                for (PalletModel model : palletModelList) {
//                    invOnHandModelList.addAll(invOnHandDAO.findByPalletId(model.getId()));
//                }
//            }
//            //SN T
//            if ("T".equalsIgnoreCase(barcode)) {
//                invOnHandDAO.findByLikeSnBarcode(barcode);
//            }
//            invOnHandModelList = invOnHandDAO.findAll();
        } catch (Exception e) {
            log.debug("Exception during findInvOnHand ", e);
        }
        return invOnHandModelList;
    }
    public void printReport(){

    }

    public void delete(int stockMoveInId){
        try {
            stockMovementInDAO.delete(stockMovementInDAO.findByID(stockMoveInId));
        } catch (Exception e) {
            log.debug("Exception error e : ", e);
        }
    }

    public void save(String productSearch, List<InvOnHandModel> invOnHandModelList, int stockInoutId){

        StockMovementInModel stockMovementInModel;
        int staffModel = (int) FacesUtil.getSession(false).getAttribute(AttributeName.STAFF.getName());
        try{
            for (InvOnHandModel invOnHandModel : invOnHandModelList){
                stockMovementInModel = new StockMovementInModel();

                if (productSearch.contains("PL")) {
                    stockMovementInModel.setSnBarcode(invOnHandModel.getSnBarcode());
                }

                stockMovementInModel.setStatus(1);
                stockMovementInModel.setStockInOutModel(stockInOutDAO.findByID(stockInoutId));
                stockMovementInModel.setBatchNo(invOnHandModel.getBatchNo());
                stockMovementInModel.setIsValid(0);
                stockMovementInModel.setCreateBy(staffModel);
                stockMovementInModel.setCreateDate(Utils.currentDate());
                stockMovementInModel.setUpdateBy(staffModel);
                stockMovementInModel.setUpdateDate(Utils.currentDate());
            }
        } catch (Exception e){
            log.debug("Exception error save : ", e);
        }
    }

    public void saveStockMovement(List<InvOnHandModel> invOnHandModelList, boolean isPalletBarcode){
        int staffModel = (int) FacesUtil.getSession(false).getAttribute(AttributeName.STAFF.getName());

        StockMovementOutModel stockMovementOutModel;
        try{
            Date currentDate = Utils.currentDate();
            for (InvOnHandModel invOnHandModel : invOnHandModelList){
                stockMovementOutModel = new StockMovementOutModel();
                //TODO
                stockMovementOutDAO.persist(stockMovementOutModel);
            }
        } catch (Exception e){
            log.debug("Exception error saveStockMovement : ", e);
        }
    }
}
