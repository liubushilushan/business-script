package com.liuapi.mongodb.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * Created on 2017/8/11.
 * Title: Simple
 * Description: 草稿列表实体
 * Copyright: Copyright(c) 2016
 * Company: 杭州公共交通云科技有限公司
 *
 * @author yangbb
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(
        ignoreUnknown = true
)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class DraftBrief implements Serializable,Comparable<DraftBrief>{
    private static final long serialVersionUID = -6193288028931761228L;

    /*-----------------------操作类型*/
    /**添加*/
    public static final Integer INSERT_OP= 1;
    /**修改*/
    public static final Integer MODIFY_OP= 2;
    /**删除*/
    public static final Integer DELETE_OP= 3;

    public static final Integer OPEN_OP= 4;

    public static final Integer STOP_OP= 5;

    /*-----------------------数据类型*/
    /**线路类型*/
    public static final Integer ROUTE_TYPE = 1;
    /**站点类型*/
    public static final Integer STOP_TYPE = 2;
    /**车辆类型*/
    public static final Integer BUS_TYPE = 3;
    /**场站类型*/
    public static final Integer POT_TYPE = 4;

    /*-----------------------审核结果和状态*/
    /**1编辑中*/
    public static final Integer EDITING = 1;
    /**2审核中*/
    public static final Integer MAINTAINING = 2;
    /**3审核成功*/
    public static final Integer MAINTAIN_SUCCESS = 3;
    /**4审核失败(驳回)*/
    public static final Integer MAINTAIN_FAIL = 4;

    /**PK*/
    protected Long id;
    /**名称*/
    protected String name;
    /**作者(申请人)*/
    protected String author;
    /**版本*/
    protected Long version;
    /**状态*/
    protected String status;
    /**最后一次编辑的时间*/
    protected Long  lastEditTime;
    /**数据类型文本描述*/
    protected String dataTypeText;
    /**数据类型*/
    protected Integer entityType;
    /**申请人*/
    protected Long applicantId;
    /**申请人名字*/
    protected String applicantName;
    /**审核人*/
    protected Long approverId;
    /**审核人名字*/
    protected String approverName;
    /**申请时间*/
    protected Date applicantTime;
    /**审核时间*/
    protected Date approverTime;
    /**审核结果*/
    protected Integer maintainResult;
    protected String maintainResultName;
    protected Integer type;
    /** 审核人的的备注信息*/
    protected String  remarkInfo;

    /**审核拒绝的信息*/
    protected String opRemark;

    /**作者操作的数据的备注信息*/
    protected String remark;
    /**操作类型*/
    protected Integer opType;
    protected String opTypeName;
    /*发布时间*/
    protected String releaseTimeName;
    /**发布时间*/
    protected Date releaseTime;
    /**记录草稿的编辑版本*/
    protected Long updateVersion;


    public DraftBrief(Long id, String name) {
        this.id = id;
        this.name = name;
    }



    public DraftBrief(Long id, String name, String author, Long version, Long lastEditTime, String remarkInfo,
                      Integer entityType, String status, Integer maintainResult) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.version = version;
        this.lastEditTime = lastEditTime;
        this.opRemark = remarkInfo;
        this.entityType = entityType;
        this.status=status;
        this.maintainResult=maintainResult;
    }
    public DraftBrief(Long id, String name, String author, Long version, Long lastEditTime, String remarkInfo,
                      Integer entityType, String status, Integer maintainResult,Integer type) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.version = version;
        this.lastEditTime = lastEditTime;
        this.remarkInfo = remarkInfo;
        this.entityType = entityType;
        this.status=status;
        this.maintainResult=maintainResult;
        this.type=type;
    }




    public DraftBrief(Long id, String name,  Long version, Long lastEditTime, String remark,
                      Integer entityType, String status, Integer maintainResult) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.lastEditTime = lastEditTime;
        this.remarkInfo = remark;
        this.entityType = entityType;
        this.status=status;
        this.maintainResult=maintainResult;
    }

    public DraftBrief(Long id, String name,   Long lastEditTime, String remarkInfo,
                      Integer entityType, Integer maintainResult ,Integer type) {
        this.id = id;
        this.name = name;
        this.lastEditTime = lastEditTime;
        this.opRemark = remarkInfo;
        this.entityType = entityType;
        this.maintainResult=maintainResult;
        this.type=type;
    }

    public DraftBrief(Long id , String  name, String author, Long version, String status, Long lastEditTime){
        this.id=id;
        this.name=name;
        this.author=author;
        this.version=version;
        this.status=status;
        this.lastEditTime=lastEditTime;
    }


    @Override
    public int compareTo(DraftBrief draftBrief) {
        Date applicantTime = draftBrief.getApplicantTime();
        Date time = this.applicantTime;
        return applicantTime.compareTo(time);
    }
}
