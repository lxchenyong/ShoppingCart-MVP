package com.chenyong.jeff.shoppingcart_mvp.view.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;


import com.chenyong.jeff.shoppingcart_mvp.R;
import com.chenyong.jeff.shoppingcart_mvp.interfacecontract.InterfaceContract;
import com.chenyong.jeff.shoppingcart_mvp.model.bean.GoodsInfo;
import com.chenyong.jeff.shoppingcart_mvp.model.bean.StoreInfo;
import com.chenyong.jeff.shoppingcart_mvp.model.biz.ShoppingCartBiz;
import com.chenyong.jeff.shoppingcart_mvp.presenter.ShoppingCartPresenter;
import com.chenyong.jeff.shoppingcart_mvp.view.adapter.ShopCartAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 购物车
 * Created by chenyong on 2016/8/25.
 */
public class ShoppingCartActivity extends AppCompatActivity implements InterfaceContract.IShoppingCartView, ShopCartAdapter.ModifyCountInterface,
        ShopCartAdapter.CheckInterface, View.OnClickListener {
    private List<StoreInfo> groups = new ArrayList<>();// 组元素数据列表
    private Map<String, List<GoodsInfo>> children = new HashMap<>();//
    private ShopCartAdapter adapter;
    private InterfaceContract.IShoppingCartPresenter presenter;
    private CheckBox allCheckbox;
    private TextView tvTotalPrice;
    private Button tvGoToPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_cart);
        ShoppingCartBiz shoppingCartBiz = new ShoppingCartBiz();
        presenter = new ShoppingCartPresenter(this, shoppingCartBiz);
        groups = presenter.initGroups();
        children = presenter.initChildren();
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        ExpandableListView listView = (ExpandableListView) findViewById(R.id.exListView);
        adapter = new ShopCartAdapter(groups, children, this);
        adapter.setCheckInterface(this);
        adapter.setModifyCountInterface(this);
        assert listView != null;
        listView.setAdapter(adapter);
        for (int i = 0; i < adapter.getGroupCount(); i++) {
            listView.expandGroup(i);// 关键步骤3,初始化时，将ExpandableListView以展开的方式呈现
        }
        allCheckbox = (CheckBox) findViewById(R.id.all_chekbox);
        assert allCheckbox != null;
        allCheckbox.setOnClickListener(this);

        tvTotalPrice = (TextView) findViewById(R.id.tv_total_price);
        tvGoToPay = (Button) findViewById(R.id.tv_go_to_pay);
        tvGoToPay.setClickable(false);
//        tvGoToPay.setLinksClickable(false);

//      LinearLayout llCart = (LinearLayout) findViewById(R.id.ll_cart);
//      LinearLayout cart_empty = (LinearLayout) findViewById(R.id.layout_cart_empty);

        assert tvGoToPay != null;
        tvGoToPay.setOnClickListener(this);
    }

    private void changeChilder(int groupPosition, int childPosition, View showCountView, boolean isIncrease) {
        GoodsInfo product = (GoodsInfo) adapter.getChild(groupPosition, childPosition);
        int currentCount = product.getCount();
        if (isIncrease) {
            currentCount++;
        } else {
            if (currentCount == 1)
                return;
            currentCount--;
        }
        product.setCount(currentCount);
        ((TextView) showCountView).setText(String.valueOf(currentCount));
//        adapter.notifyDataSetChanged();
        presenter.changeCount(product.getId(), currentCount);
        presenter.showTotalPrice(groups, children);
    }

    @Override
    public void doIncrease(int groupPosition, int childPosition, View showCountView, boolean isChecked) {
        changeChilder(groupPosition, childPosition, showCountView, true);
    }

    @Override
    public void doDecrease(int groupPosition, int childPosition, View showCountView, boolean isChecked) {
        changeChilder(groupPosition, childPosition, showCountView, false);
    }

    @Override
    public void childDelete(int groupPosition, int childPosition) {
        List<GoodsInfo> goodsInfo = children.get(groups.get(groupPosition).getId());
        presenter.deleteGoodsInfo(goodsInfo.get(childPosition).getId());
        goodsInfo.remove(childPosition);
        StoreInfo group = groups.get(groupPosition);
        List<GoodsInfo> childs = children.get(group.getId());
        if (childs.size() == 0) {
            presenter.deleteStoreInfo(groups.get(groupPosition).getId());
            groups.remove(groupPosition);

        }
        presenter.showTotalPrice(groups, children);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void checkGroup(int groupPosition, boolean isChecked) {
        StoreInfo group = groups.get(groupPosition);
        List<GoodsInfo> childs = children.get(group.getId());
        for (int i = 0; i < childs.size(); i++) {
            childs.get(i).setChoosed(isChecked);
        }
        showAllCheck(isAllCheck());
        presenter.showTotalPrice(groups, children);
    }

    @Override
    public void checkChild(int groupPosition, int childPosition, boolean isChecked) {
        boolean allChildSameState = true;// 判断改组下面的所有子元素是否是同一种状态
        StoreInfo group = groups.get(groupPosition);
        List<GoodsInfo> childs = children.get(group.getId());
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
        showAllCheck(isAllCheck());
        presenter.showTotalPrice(groups, children);
    }

    /**
     * @return 上家是否全勾选
     */
    private boolean isAllCheck() {
        if (groups.size() == 0)
            return false;
        for (StoreInfo group : groups) {
            if (!group.isChoosed())
                return false;
        }
        return true;
    }

    /**
     * 全选是否勾选
     *
     * @param isAllCheck 商家是否全勾选
     */
    private void showAllCheck(boolean isAllCheck) {
        if (isAllCheck)
            allCheckbox.setChecked(true);
        else
            allCheckbox.setChecked(false);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showProgressBar(boolean isFlag) {
        if (isFlag) {
            //此处显示进度条代码
        } else {
            //此处关闭进度条代码
        }
    }

    @Override
    public void showTotalPriceText(double totalPrice) {
        if (totalPrice != 0)
            tvGoToPay.setClickable(false);
        else
            tvGoToPay.setClickable(false);
        tvTotalPrice.setText(String.format("￥%.2f", totalPrice));
//        tvGoToPay.setText(String.format("去支付(%d)",totalCount));
    }

    @Override
    public void forwardToNextView() {
        //TODO 下单之后跳转到下一个页面  及刷新当前页面
        showAllCheck(isAllCheck());
        presenter.showTotalPrice(groups, children);
    }

    @Override
    public void showErrorMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showCreateButton(boolean isChecked) {
        tvGoToPay.setClickable(isChecked);
        Log.d("button-2-", "button··" + tvGoToPay.getLinksClickable());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.all_chekbox:
                for (int i = 0; i < groups.size(); i++) {
                    groups.get(i).setChoosed(allCheckbox.isChecked());
                    StoreInfo group = groups.get(i);
                    List<GoodsInfo> childs = children.get(group.getId());
                    for (int j = 0; j < childs.size(); j++) {
                        childs.get(j).setChoosed(allCheckbox.isChecked());
                    }
                }
                presenter.showTotalPrice(groups, children);
                adapter.notifyDataSetChanged();
                break;
            case R.id.tv_go_to_pay:
//                List<GoodsInfo> payGoodsInfos = new ArrayList<>();
//                for (List<GoodsInfo> goodsInfos : children.values()) {
//                    for (int i = 0; i < goodsInfos.size(); i++) {
//                        if (goodsInfos.get(i).isChoosed()) {
//                            payGoodsInfos.add(goodsInfos.get(i));
//                        }
//                    }
//                }
//                presenter.shouButton();
                presenter.createOrder(groups, children);
                break;
        }
    }

}
