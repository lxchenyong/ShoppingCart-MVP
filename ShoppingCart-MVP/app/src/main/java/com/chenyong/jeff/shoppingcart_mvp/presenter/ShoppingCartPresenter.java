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

    private double totalPrice = 0.00;
    private List<StoreInfo> stores = new ArrayList<>();// 组元素数据列表
    private Map<String, List<GoodsInfo>> goods = new HashMap<>();//

    public ShoppingCartPresenter(InterfaceContract.IShoppingCartView iShoppingCartView, ShoppingCartBiz shoppingCartBiz) {
        this.iShoppingCartView = iShoppingCartView;
        this.shoppingCartBiz = shoppingCartBiz;
    }

    /**
     * 拿到所有商家
     */
    private void initGroups() {
        stores.addAll(shoppingCartBiz.initGroups());
    }

    /**
     * 拿到商家下所有商品
     */
    private void initChildren() {
        goods.putAll(shoppingCartBiz.initChildren());
    }

    /**
     * @return 是否全选
     */
    private boolean isAllCheck() {
        if (stores.size() == 0)
            return false;
        for (StoreInfo group : stores) {
            if (!group.isChoosed())
                return false;
        }
        return true;
    }

    @Override
    public void initData() {
        initGroups();
        initChildren();
        iShoppingCartView.showData(stores, goods);
    }


    @Override
    public void showChangeGroupCheckedTotalPrice(int groupPosition, boolean isChecked) {
        StoreInfo group = stores.get(groupPosition);
        List<GoodsInfo> childs = goods.get(group.getId());
        for (int i = 0; i < childs.size(); i++) {
            childs.get(i).setChoosed(isChecked);
        }
        iShoppingCartView.showAllCheck(isAllCheck());
        calculateTotalMoney(stores, goods);
        iShoppingCartView.showTotalPriceText(totalPrice);
    }

    @Override
    public void showChangeChilderCheckedTotalPrice(int groupPosition, int childPosition, boolean isChecked) {
        boolean allChildSameState = true;// 判断改组下面的所有子元素是否是同一种状态
        StoreInfo group = stores.get(groupPosition);
        List<GoodsInfo> childs = goods.get(group.getId());
        for (int i = 0; i < childs.size(); i++) {
            // 不全选中
            if (childs.get(i).isChoosed() != isChecked) {
                allChildSameState = false;
                break;
            }
        }
        //获取店铺选中商品的总金额
        if (allChildSameState) {
            group.setChoosed(isChecked);// 如果所有子元素状态相同，那么对应的组元素被设为这种统一状态
        } else {
            group.setChoosed(false);// 否则，组元素一律设置为未选中状态
        }
        iShoppingCartView.showAllCheck(isAllCheck());
        calculateTotalMoney(stores, goods);
        iShoppingCartView.showTotalPriceText(totalPrice);
    }

    @Override
    public void showAllCheckedTotalPrice(boolean isChecked) {
        for (int i = 0; i < stores.size(); i++) {
            stores.get(i).setChoosed(isChecked);
            StoreInfo group = stores.get(i);
            List<GoodsInfo> childs = goods.get(group.getId());
            for (int j = 0; j < childs.size(); j++) {
                childs.get(j).setChoosed(isChecked);
            }
        }
        calculateTotalMoney(stores, goods);
        iShoppingCartView.showTotalPriceText(totalPrice);
        iShoppingCartView.showData(stores, goods);
    }

    @Override
    public void changeCount(String groupID, String goodsId, int count) {
        //TODO 根据ID 修改 db 里的数量
        for (int i = 0; i < stores.size(); i++) {
            if (groupID.equals(stores.get(i).getId())) {
                for (int j = 0; j < i; j++) {
                    if (goodsId.equals(goods.get(stores.get(i).getId()).get(j).getId())) {
                        goods.get(stores.get(i).getId()).get(j).setCount(count);
                    }
                }
            }
        }
        calculateTotalMoney(stores, goods);
        iShoppingCartView.showTotalPriceText(totalPrice);

    }

    @Override
    public void deleteGoodsInfo(int groupPosition, int childPosition) {
        //TODO 根据ID 删除 db 里的商品
        List<GoodsInfo> goodsInfo = goods.get(stores.get(groupPosition).getId());
        StoreInfo group = stores.get(groupPosition);
        goodsInfo.remove(childPosition);
        List<GoodsInfo> childs = goods.get(group.getId());
        if (childs.size() == 0) {
            stores.remove(groupPosition);

        }
        calculateTotalMoney(stores, goods);
        iShoppingCartView.showTotalPriceText(totalPrice);
        iShoppingCartView.showData(stores, goods);
    }

    /**
     * 创建订单
     */
    @Override
    public void createOrder() {
        iShoppingCartView.showProgressBar(true);
        //TODO 这里就向服务器发送信息 成功之后从db中删除及跳转到下一个页面
        iShoppingCartView.showProgressBar(false);
        iShoppingCartView.forwardToNextView();
        doBuyDelete();

        //TODO 创建失败
        iShoppingCartView.showProgressBar(false);
        String errorMsg = "创建订单失败";
        iShoppingCartView.showErrorMessage(errorMsg);
    }

    /**
     * 购买成功之后删除
     */
    private void doBuyDelete() {
        List<StoreInfo> toBeDeleteGroups = new ArrayList<>();
        for (int i = 0; i < stores.size(); i++) {
            StoreInfo group = stores.get(i);
            if (group.isChoosed()) {
                toBeDeleteGroups.add(group);
            }
            List<GoodsInfo> toBeDeleteProducts = new ArrayList<>();
            List<GoodsInfo> childs = goods.get(group.getId());
            for (int j = 0; j < childs.size(); j++) {
                if (childs.get(j).isChoosed()) {
                    toBeDeleteProducts.add(childs.get(j));
                }
            }
            childs.removeAll(toBeDeleteProducts);
        }
        stores.removeAll(toBeDeleteGroups);

        calculateTotalMoney(stores, goods);
        iShoppingCartView.showTotalPriceText(totalPrice);
        iShoppingCartView.showData(stores, goods);
    }

    /**
     * 计算勾选总金额
     */
    private void calculateTotalMoney(List<StoreInfo> storeInfo, Map<String, List<GoodsInfo>> goodsinfo) {
        //        int totalCount = 0;
        totalPrice = 0.00;
        for (int i = 0; i < storeInfo.size(); i++) {
            StoreInfo group = storeInfo.get(i);
            List<GoodsInfo> goods = goodsinfo.get(group.getId());
            for (int j = 0; j < goods.size(); j++) {
                GoodsInfo product = goods.get(j);
                if (product.isChoosed()) {
//                    totalCount++;
                    totalPrice += product.getPrice() * product.getCount();
                }
            }
        }
    }
}
