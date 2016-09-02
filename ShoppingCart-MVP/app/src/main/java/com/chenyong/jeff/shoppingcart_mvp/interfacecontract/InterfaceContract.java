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
         * 显示总金额
         *
         * @param totalPrice 总金额
         */
        void showTotalPriceText(double totalPrice);

        /**
         * 跳转到下一个页面
         */
        void forwardToNextView();

    }


    /**
     * 购物车 interface presenter
     */
    interface IShoppingCartPresenter {
        /**
         * @return 初始化所有商家
         */
        List<StoreInfo> initGroups();

        /**
         * @return 初始化所有商品
         */
        Map<String, List<GoodsInfo>> initChildrens();

        /**
         * 显示总金额
         */
        void showTotalPrice();

        /**
         * 创建订单
         *
         * @param payGoodsInfos 勾选的商品
         */
        void createOrder(List<GoodsInfo> payGoodsInfos);
    }
}
