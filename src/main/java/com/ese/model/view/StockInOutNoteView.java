package com.ese.model.view;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

@Getter
@Setter
public class StockInOutNoteView {

    private int id;
    private String type;
    private String inoutCode;
    private String inoutNote;
    private String remark;
    private Integer isValid;
    private Integer version;
    private int createBy;
    private Date createDate;
    private int updateBy;
    private Date updateDate;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", id)
                .append("type", type)
                .append("inoutCode", inoutCode)
                .append("inoutNote", inoutNote)
                .append("remark", remark)
                .append("isValid", isValid)
                .append("version", version)
                .append("createBy", createBy)
                .append("createDate", createDate)
                .append("updateBy", updateBy)
                .append("updateDate", updateDate)
                .toString();
    }
}
