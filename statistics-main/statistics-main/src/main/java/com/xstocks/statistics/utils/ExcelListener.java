package com.xstocks.statistics.utils;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ExcelListener<T extends BaseRowModel> extends AnalysisEventListener<T> {

    private List<T> rows = new ArrayList<>();

    /**
     * object是每一行数据映射的对象
     * @param object
     * @param context
     */
    @Override
    public void invoke(T object, AnalysisContext context) {
//        System.out.println("当前行："+context.getCurrentRowNum());
//        System.out.println(object);
        rows.add(object);
        //根据自己业务做处理
        doSomething(object);
    }

    private void doSomething(T object) {
        //1、入库调用接口
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {

//        log.info("read {} rows %n", rows.size());
    }

    public List<T> getRows() {

        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

}
