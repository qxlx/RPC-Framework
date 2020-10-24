package com.ncst.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 日拱一卒，不期速成
 *
 * @Auther: i
 * @Date: 2020/10/19/16:11
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataObject implements Serializable {

    private Integer id;

    private String message;

}
