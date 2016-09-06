package com.chenyong.jeff.shoppingcart_mvp.presenter;


import com.chenyong.jeff.shoppingcart_mvp.interfacecontract.InterfaceContract;
import com.chenyong.jeff.shoppingcart_mvp.model.bean.GoodsInfo;
import com.chenyong.jeff.shoppingcart_mvp.model.bean.StoreInfo;
import com.chenyong.jeff.shoppingcart_mvp.model.biz.ShoppingCartBiz;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 购物车 presenter
 * Created by chenyong on 2016/8/25.
 */
public class ShoppingCartPresenter implements InterfaceContract.IShoppingCartPresenter {
    private ShoppingCartBiz shoppingCartBiz;
    private InterfaceContract.IShoppingCartView iShoppingCartView;

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
        return shoppingCartBiz.initGroups();
    }

    /**
     * @return 返回商家下所有商品
     */
    @Override
    public Map<String, List<GoodsInfo>> initChildren() {
        return shoppingCartBiz.initChildren();
    }

    /**
     * 显示总金额
     */
    @Override
    public void showTotalPrice(List<StoreInfo> stores, Map<String, List<GoodsInfo>> goods) {
        calculateTotalMoney(stores, goods);
        iShoppingCartView.showTotalPriceText(totalPrice);
//        if (totalPrice != 0)
//            iShoppingCartView.showCreateButton(true);
//        else
//            iShoppingCartView.showCreateButton(false);
    }

    @Override
    public void changeCount(String goodsId, int count) {
        //TODO 根据ID 修改 db 里的数量
    }

    @Override
    public void deleteGoodsInfo(String goodsId) {
        //TODO 根据ID 删除 db 里的商品
    }

    @Override
    public void deleteStoreInfo(String storeId) {
        //TODO 根据ID 删除 db 里的商家
    }

    /**
     * 创建订单
     *
     * @param stores 商家
     * @param goods  商品
     */
    @Override
    public void createOrder(List<StoreInfo> stores, Map<String, List<GoodsInfo>> goods) {
        iShoppingCartView.showProgressBar(true);
        //TODO 这里就向服务器发送信息 成功之后从db中删除及跳转到下一个页面
        iShoppingCartView.showProgressBar(false);
        iShoppingCartView.forwardToNextView();
        doBuyDelete(stores, goods);
        //TODO 创建失败
        iShoppingCartView.showProgressBar(false);
        String errorMsg = "创建订单失败";
        iShoppingCartView.showErrorMessage(errorMsg);
    }

    /**
     * 购买成功之后删除
     */
    public void doBuyDelete(List<StoreInfo> storeInfos, Map<String, List<GoodsInfo>> goodinfos) {
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
    private void calculateTotalMoney(List<StoreInfo> storeInfo, Map<String, List<GoodsInfo>> goodinfo) {
        //        int totalCount = 0;
        totalPrice = 0.00;
        for (int i = 0; i < storeInfo.size(); i++) {
            StoreInfo group = storeInfo.get(i);
            List<GoodsInfo> childs = goodinfo.get(group.getId());
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
