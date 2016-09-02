package com.chenyong.jeff.shoppingcart_mvp.presenter;


import com.chenyong.jeff.shoppingcart_mvp.interfacecontract.InterfaceContract;
import com.chenyong.jeff.shoppingcart_mvp.model.bean.GoodsInfo;
import com.chenyong.jeff.shoppingcart_mvp.model.bean.StoreInfo;
import com.chenyong.jeff.shoppingcart_mvp.model.biz.ShoppingCartBiz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 购物车 presenter
 * Created by chenyong on 2016/8/25.
 */
public class ShoppingCartPresenter implements InterfaceContract.IShoppingCartPresenter {
    private ShoppingCartBiz shoppingCartBiz;
    private InterfaceContract.IShoppingCartView iShoppingCartView;

    private List<StoreInfo> groups = new ArrayList<>();// 组元素数据列表
    private Map<String, List<GoodsInfo>> children = new HashMap<>();// 子元素数据列表
    //        int totalCount = 0;
    public double totalPrice = 0.00;

    public ShoppingCartPresenter(InterfaceContract.IShoppingCartView iShoppingCartView, ShoppingCartBiz shoppingCartBiz) {
        this.iShoppingCartView = iShoppingCartView;
        this.shoppingCartBiz = shoppingCartBiz;
    }

    /**
     * @return 返回所有商家
     */
    @Override
    public List<StoreInfo> initGroups() {
        groups = shoppingCartBiz.initGroups();
        return groups;
    }

    /**
     * @return 返回商家下所有商品
     */
    @Override
    public Map<String, List<GoodsInfo>> initChildrens() {
        children = shoppingCartBiz.initChildrens();
        return children;
    }

    /**
     * 显示总金额
     */
    @Override
    public void showTotalPrice() {
        calculateTotalMoney(groups,children);
        iShoppingCartView.showTotalPriceText(totalPrice);
    }

    /**
     * 创建订单
     *
     * @param payGoodsInfos 所有勾选的商品
     */
    @Override
    public void createOrder(List<GoodsInfo> payGoodsInfos) {
        //TODO 这里就向服务器发送信息 成功之后从db中删除及跳转到下一个页面
        doBuyDelete(groups,children);
        iShoppingCartView.forwardToNextView();
    }

    /**
     * 购买成功之后删除
     */
    public void doBuyDelete(List<StoreInfo>storeInfos ,Map<String, List<GoodsInfo>> goodinfos) {
        List<StoreInfo> toBeDeleteGroups = new ArrayList<>();
        for (int i = 0; i < storeInfos.size(); i++) {
            StoreInfo group = storeInfos.get(i);
            if (group.isChoosed()) {
                toBeDeleteGroups.add(group);
            }
            List<GoodsInfo> toBeDeleteProducts = new ArrayList<>();
            List<GoodsInfo> childs = goodinfos.get(group.getId());
            for (int j = 0; j < childs.size(); j++) {
                if (childs.get(j).isChoosed()) {
                    toBeDeleteProducts.add(childs.get(j));
                }
            }
            childs.removeAll(toBeDeleteProducts);
        }
        storeInfos.removeAll(toBeDeleteGroups);
    }

    /**
     * 计算勾选总金额
     */
    public void calculateTotalMoney(List<StoreInfo>storeInfos ,Map<String, List<GoodsInfo>> goodinfos) {
        //        int totalCount = 0;
        totalPrice = 0.00;
        for (int i = 0; i < storeInfos.size(); i++) {
            StoreInfo group = storeInfos.get(i);
            List<GoodsInfo> childs = goodinfos.get(group.getId());
            for (int j = 0; j < childs.size(); j++) {
                GoodsInfo product = childs.get(j);
                if (product.isChoosed()) {
//                    totalCount++;
                    totalPrice += product.getPrice() * product.getCount();
                }
            }
        }
    }
}
