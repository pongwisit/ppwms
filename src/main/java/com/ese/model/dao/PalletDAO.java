package com.ese.model.dao;

import com.ese.model.db.PalletModel;
import com.ese.model.view.report.PalletManagemengModelReport;
import com.ese.model.view.report.PalletSubReport;
import com.ese.utils.Utils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PalletDAO extends GenericDAO<PalletModel, Integer>{

    public List<PalletModel> findPalletTable(){
        log.debug("findOnloadPallet().");
        List<PalletModel> palletModelList = Utils.getEmptyList();
        try {
            Criteria criteria = getCriteria();
            criteria.add(Restrictions.eq("status", 2));
            palletModelList = Utils.safetyList(criteria.list());
            log.debug("findOnloadPallet Size : {}", palletModelList.size());
            return palletModelList;
        } catch (Exception e){
            log.debug("Exception : {}", e);
            return palletModelList;
        }
    }

    public List<PalletModel> findChang(int statusId, int warehouse, int conveyorLine, int location, String keyItemDescription, int combine){
        log.debug("findChang().");
        List<PalletModel> palletModelList = Utils.getEmptyList();
        try {
            Criteria criteria = getSession().createCriteria(PalletModel.class, "p");

            if (!Utils.isZero(warehouse)){
                criteria.add(Restrictions.eq("msWarehouseModel.id", warehouse));
            }

            if (!Utils.isZero(conveyorLine)){
                criteria.add(Restrictions.eq("msWorkingAreaModel.id", conveyorLine));
            }

            if (!Utils.isZero(location)){
                criteria.add(Restrictions.eq("msLocationModel.id", location));
            }

            criteria.add(Restrictions.eq("isCombine", combine));

            if (!Utils.isNull(keyItemDescription) && !"".equalsIgnoreCase(keyItemDescription)){
                criteria.createAlias("p.msItemModel", "c");
                Criterion itemDes = Restrictions.like("c.dSGThaiItemDescription", "%" + keyItemDescription.trim() + "%");
                criteria.createAlias("p.msLocationModel", "d");
                Criterion locationBarcode = Restrictions.like("d.locationBarcode", "%"+keyItemDescription.trim()+"%");
                criteria.add(Restrictions.or(itemDes,locationBarcode));
            }

            if (statusId == 0){
                criteria.add(Restrictions.eq("status", 2));
            } else if (statusId == 2){
                criteria.add(Restrictions.lt("status", 3));
            } else if (statusId == 3){
                criteria.add(Restrictions.eq("qty", 0));
            }

            criteria.add(Restrictions.ne("status", 6));
            criteria.add(Restrictions.eq("isValid", 1));
            criteria.addOrder(Order.desc("updateDate"));
            palletModelList = Utils.safetyList(criteria.list());
            log.debug("findOnloadPallet Size : {}", palletModelList.size());
            return palletModelList;
        } catch (Exception e){
            log.debug("Exception : {}", e);
            return palletModelList;
        }
    }

    public void updauePalletByChangeLocation(int palletId, int locationId){
        log.debug("updauePalletByChangeLocation().");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" UPDATE ").append(getPrefix()).append(".pallet SET ").append(getPrefix()).append(".pallet.location_id = ").append("'").append(locationId).append("'");
        stringBuilder.append(" WHERE ").append(getPrefix()).append(".pallet.id = ").append("'").append(palletId).append("'");

        log.debug("SQL Pallet : {}",stringBuilder.toString());
        try {
            SQLQuery q = getSession().createSQLQuery(stringBuilder.toString());
            q.executeUpdate();
        } catch (Exception e) {
            log.debug("Exception : ", e);
        }
    }

    public void updateLocationByOld(int locationId){
        log.debug("updateLocationByOld().");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" UPDATE ").append(getPrefix()).append(".location SET ").append(getPrefix()).append(".location.reserved_qty += 1 ");
        stringBuilder.append(" WHERE ").append(getPrefix()).append(".location.id = ").append("'").append(locationId).append("'");

        log.debug("SQL Location : {}", stringBuilder.toString());

        try {
            SQLQuery q = getSession().createSQLQuery(stringBuilder.toString());
            q.executeUpdate();
        } catch (Exception e) {
            log.debug("Exception : ", e);
        }
    }

    public void updateLocationByNew(int locationId){
        log.debug("updateLocationByNew().");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" UPDATE ").append(getPrefix()).append(".location SET ").append(getPrefix()).append(".location.reserved_qty -= 1 ");
        stringBuilder.append(" WHERE ").append(getPrefix()).append(".location.id = ").append("'").append(locationId).append("'");

        log.debug("SQL Location : {}", stringBuilder.toString());

        try {
            SQLQuery q = getSession().createSQLQuery(stringBuilder.toString());
            q.executeUpdate();
        } catch (Exception e) {
            log.debug("Exception : ", e);
        }
    }

    public void updateLocationByStatusPrinted(int locationId){
        log.debug("updateLocationByStatusPrinted().");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" UPDATE ").append(getPrefix()).append(".location SET ").append(getPrefix()).append(".location.reserved_qty -= 1 ");
        stringBuilder.append(" WHERE ").append(getPrefix()).append(".location.id = ").append("'").append(locationId).append("'");

        log.debug("SQL Location : {}", stringBuilder.toString());

        try {
            SQLQuery q = getSession().createSQLQuery(stringBuilder.toString());
            q.executeUpdate();
        } catch (Exception e) {
            log.debug("Exception : ", e);
        }
    }

    public void updateLocationByStatusLocated(int locationId){
        log.debug("updateLocationByStatusLocated().");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" UPDATE ").append(getPrefix()).append(".location SET ").append(getPrefix()).append(".location.qty -= 1 ");
        stringBuilder.append(" WHERE ").append(getPrefix()).append(".location.id = ").append("'").append(locationId).append("'");

        log.debug("SQL Location : {}", stringBuilder.toString());

        try {
            SQLQuery q = getSession().createSQLQuery(stringBuilder.toString());
            q.executeUpdate();
        } catch (Exception e) {
            log.debug("Exception : ", e);
        }
    }

    public List<PalletManagemengModelReport> genSQLReportPallet(int palletId){
        log.debug("genSQLReportPallet(). {}", palletId);
        List<PalletManagemengModelReport> reportViews = new ArrayList<PalletManagemengModelReport>();
        StringBuilder sqlBuilder = new StringBuilder();

        sqlBuilder.append(" SELECT DISTINCT");
        sqlBuilder.append(" ").append(getPrefix()).append(".item_master.DSGThaiItemDescription AS DESCRIPTION,");
        sqlBuilder.append(" ").append(getPrefix()).append(".warehouse.warehouse_code AS WAREHOUSE_CODE,");
        sqlBuilder.append(" ").append(getPrefix()).append(".pallet.pallet_barcode AS PALLET_BARCODE,");
        sqlBuilder.append(" ").append(getPrefix()).append(".location.location_barcode AS LOCATION_BARCODE,");
        sqlBuilder.append(" ").append(getPrefix()).append(".pallet.create_date AS CREATE_DATE,");
        sqlBuilder.append(" ").append(getPrefix()).append(".inv_onhand.grade AS GRADE,");
        sqlBuilder.append(" ").append(getPrefix()).append(".inv_onhand.batchno AS BATHCH_NO,");
        sqlBuilder.append(" ").append(getPrefix()).append(".working_area.name AS WORKING_NAME,");
        sqlBuilder.append(" Count(").append(getPrefix()).append(".inv_onhand.id) AS COUNT_ID");
        sqlBuilder.append(" FROM ").append(getPrefix()).append(".pallet");
        sqlBuilder.append(" LEFT JOIN ").append(getPrefix()).append(".item_master");
        sqlBuilder.append(" ON  ").append(getPrefix()).append(".pallet.item_id = ").append(getPrefix()).append(".item_master.id");
        sqlBuilder.append(" LEFT JOIN ").append(getPrefix()).append(".warehouse");
        sqlBuilder.append(" ON ").append(getPrefix()).append(".pallet.warehouse_id = ").append(getPrefix()).append(".warehouse.id");
        sqlBuilder.append(" LEFT JOIN ").append(getPrefix()).append(".working_area");
        sqlBuilder.append(" ON ").append(getPrefix()).append(".pallet.conveyor_line = ").append(getPrefix()).append(".working_area.id");
        sqlBuilder.append(" LEFT JOIN ").append(getPrefix()).append(".location");
        sqlBuilder.append(" ON ").append(getPrefix()).append(".pallet.location_id = ").append(getPrefix()).append(".location.id");
        sqlBuilder.append(" LEFT JOIN ").append(getPrefix()).append(".inv_onhand");
        sqlBuilder.append(" ON ").append(getPrefix()).append(".pallet.id = ").append(getPrefix()).append(".inv_onhand.pallet_id");
        sqlBuilder.append(" WHERE ").append(getPrefix()).append(".pallet.ID = " + palletId );
        sqlBuilder.append(" GROUP BY ").append(getPrefix()).append(".item_master.DSGThaiItemDescription,");
        sqlBuilder.append(" ").append(getPrefix()).append(".warehouse.warehouse_code,").append(getPrefix()).append(".pallet.pallet_barcode,");
        sqlBuilder.append(" ").append(getPrefix()).append(".location.location_barcode,").append(getPrefix()).append(".pallet.create_date,").append(getPrefix()).append(".inv_onhand.grade,");
        sqlBuilder.append(" ").append(getPrefix()).append(".working_area.name,").append(getPrefix()).append(".inv_onhand.batchno");

        log.debug(sqlBuilder.toString());

        try {
            SQLQuery query = getSession().createSQLQuery(sqlBuilder.toString())
                    .addScalar("DESCRIPTION", StringType.INSTANCE)
                    .addScalar("WAREHOUSE_CODE", StringType.INSTANCE)
                    .addScalar("PALLET_BARCODE", StringType.INSTANCE)
                    .addScalar("LOCATION_BARCODE", StringType.INSTANCE)
                    .addScalar("CREATE_DATE", TimestampType.INSTANCE)
                    .addScalar("GRADE", StringType.INSTANCE)
                    .addScalar("BATHCH_NO", StringType.INSTANCE)
                    .addScalar("WORKING_NAME", StringType.INSTANCE)
                    .addScalar("COUNT_ID", IntegerType.INSTANCE);
            List<Object[]> objects = query.list();

            for (Object[] entity : objects) {
                PalletManagemengModelReport report = new PalletManagemengModelReport();
                report.setDSGThaiItemDescription(Utils.parseString(entity[0], ""));
                report.setWarehouseCode(Utils.parseString(entity[1], ""));
                report.setPalletBarcode(Utils.parseString(entity[2], ""));
                report.setLocationBarcode(Utils.parseString(entity[3], ""));
                report.setCreateDate(Utils.convertToStringDDMMYYYY(Utils.parseDate(entity[4], null)));
                report.setGrade(Utils.parseString(entity[5], ""));
                report.setBathcgNo(Utils.parseString(entity[6], ""));
                report.setWorkingName(Utils.parseString(entity[7], ""));
                report.setCountId(Utils.parseInt(entity[8], 0));
                reportViews.add(report);
            }
        } catch (Exception e) {
            log.debug("Exception SQL : {}", e);
        }

        return reportViews;
    }

    public PalletModel findByIdToReport(int palletId){
        log.debug("findOnloadPallet().");
        PalletModel palletModel = new PalletModel();
        try {
            Criteria criteria = getCriteria();
            criteria.add(Restrictions.eq("id", palletId));
            palletModel = (PalletModel) criteria.uniqueResult();
            return palletModel;
        } catch (Exception e){
            log.debug("Exception : {}", e);
            return palletModel;
        }
    }

    public List<PalletSubReport> genSQLReportPalletV2(int palletId){
        log.debug("genSQLReportPallet(). {}", palletId);
        List<PalletSubReport> reportViews = new ArrayList<PalletSubReport>();
        StringBuilder sqlBuilder = new StringBuilder();

        sqlBuilder.append("SELECT ");
        sqlBuilder.append(" Count(").append(getPrefix()).append(".inv_onhand.id) AS QTY,");
        sqlBuilder.append(" ").append(getPrefix()).append(".item_master.ItemId AS ITEM_ID,");
        sqlBuilder.append(" ").append(getPrefix()).append(".item_master.DSGThaiItemDescription AS ITEM_DESCRIPTION,");
        sqlBuilder.append(" ").append(getPrefix()).append(".shift.name AS SHIFT_NAME,");
        sqlBuilder.append(" ").append(getPrefix()).append(".working_area.name AS WORKING_AREA_NAME,");
        sqlBuilder.append(" ").append(getPrefix()).append(".inv_onhand.batchno AS BATCHNO");
        sqlBuilder.append(" FROM ").append(getPrefix()).append(".inv_onhand");
        sqlBuilder.append(" LEFT JOIN ").append(getPrefix()).append(".item_master");
        sqlBuilder.append(" ON  ").append(getPrefix()).append(".inv_onhand.item_id = ").append(getPrefix()).append(".item_master.id");
        sqlBuilder.append(" LEFT JOIN ").append(getPrefix()).append(".pallet");
        sqlBuilder.append(" ON  ").append(getPrefix()).append(".inv_onhand.pallet_id = ").append(getPrefix()).append(".pallet.id");
        sqlBuilder.append(" LEFT JOIN ").append(getPrefix()).append(".shift");
        sqlBuilder.append(" ON  ").append(getPrefix()).append(".pallet.shift_id = ").append(getPrefix()).append(".shift.id");
        sqlBuilder.append(" LEFT JOIN ").append(getPrefix()).append(".working_area");
        sqlBuilder.append(" ON  ").append(getPrefix()).append(".inv_onhand.working_area_id = ").append(getPrefix()).append(".working_area.id");
        sqlBuilder.append(" WHERE ").append(getPrefix()).append(".inv_onhand.pallet_id = " + palletId );
        sqlBuilder.append(" GROUP BY ").append(getPrefix()).append(".item_master.ItemId,");
        sqlBuilder.append(" ").append(getPrefix()).append(".item_master.DSGThaiItemDescription,").append(getPrefix()).append(".shift.name,");
        sqlBuilder.append(" ").append(getPrefix()).append(".working_area.name,").append(getPrefix()).append(".inv_onhand.batchno");

        log.debug(sqlBuilder.toString());

        try {
            SQLQuery query = getSession().createSQLQuery(sqlBuilder.toString())
                    .addScalar("QTY", IntegerType.INSTANCE)
                    .addScalar("ITEM_ID", StringType.INSTANCE)
                    .addScalar("ITEM_DESCRIPTION", StringType.INSTANCE)
                    .addScalar("SHIFT_NAME", StringType.INSTANCE)
                    .addScalar("WORKING_AREA_NAME", StringType.INSTANCE)
                    .addScalar("BATCHNO", StringType.INSTANCE);
            List<Object[]> objects = query.list();

            int i = 1;

            for (Object[] entity : objects) {
                PalletSubReport report = new PalletSubReport();
                report.setNo(i++);
                report.setQty(Utils.parseInt(entity[0], 0));
                report.setItemCode(Utils.parseString(entity[1], ""));
                report.setItemDescription(Utils.parseString(entity[2], ""));
                report.setShiftName(Utils.parseString(entity[3], ""));
                report.setWorkingAreaName(Utils.parseString(entity[4], ""));
                report.setBatchNo(Utils.parseString(entity[5], ""));
                reportViews.add(report);
            }
        } catch (Exception e) {
            log.debug("Exception SQL : {}", e);
        }

        return reportViews;
    }
}
