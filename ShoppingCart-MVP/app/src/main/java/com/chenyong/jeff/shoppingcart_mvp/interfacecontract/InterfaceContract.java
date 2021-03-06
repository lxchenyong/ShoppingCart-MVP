package com.chenyong.jeff.shoppingcart_mvp.interfacecontract;

import com.chenyong.jeff.shoppingcart_mvp.model.bean.GoodsInfo;
import com.chenyong.jeff.shoppingcart_mvp.model.bean.StoreInfo;

import java.util.List;
import java.util.Map;

/**
 * 购物车的interface-view 和 interface-presenter
 * Created by chenyong on 2016/8/27.
 */
public interface InterfaceContract {

    /**
     * 购物车 interface view
     */
    interface IShoppingCartView {

        /**
         * 显示数据
         *
         * @param stores 所有商家
         * @param goods  所有商品
         */
        void showData(List<StoreInfo> stores, Map<String, List<GoodsInfo>> goods);

        /**
         * @param isChecked 全选是否勾上
         */
        void showAllCheck(boolean isChecked);

        /**
         * 显示总金额
         *
         * @param totalPrice 总金额
         */
        void showTotalPriceText(double totalPrice);

        /**
         * 跳转到下一个页面
         */
        void forwardToNextView();

        /**
         * 显示错误信息
         *
         * @param message 信息
         */
        void showErrorMessage(String message);

        /**
         * @param isChecked 是否显示Button
         */
        void showCreateButton(boolean isChecked);

    }


    /**
     * 购物车 interface presenter
     */
    interface IShoppingCartPresenter {

        /**
         * 初始化数据
         */
        void initData();

        /**
         * 点击商家勾选操作
         *
         * @param groupPosition 商家位置
         * @param isChecked     商家点击
         */
        void setGroupChecked(int groupPosition, boolean isChecked);

        /**
         * 点击商品勾选操作
         *
         * @param groupPosition 商家位置
         * @param childPosition 商品位置
         * @param isChecked     商品点击
         */
        void setChildrenChecked(int groupPosition, int childPosition, boolean isChecked);

        /**
         * @param isChecked 全选点击操作
         */
        void setAllChecked(boolean isChecked);


        /**
         * 修改商品数量
         *
         * @param groupID 商家ID
         * @param goodsId 商品ID
         * @param count   数量
         */
        void changeCount(String groupID, String goodsId, int count);

        /**
         * 删除当前商品
         *
         * @param groupPosition 商家索引
         * @param childPosition 商品索引
         */
        void deleteGoodsInfo(int groupPosition, int childPosition);

        /**
         * 创建订单
         */
        void createOrder();
    }
}
