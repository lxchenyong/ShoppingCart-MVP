package com.chenyong.jeff.shoppingcart_mvp;

import com.chenyong.jeff.shoppingcart_mvp.interfacecontract.InterfaceContract;
import com.chenyong.jeff.shoppingcart_mvp.model.bean.GoodsInfo;
import com.chenyong.jeff.shoppingcart_mvp.model.bean.StoreInfo;
import com.chenyong.jeff.shoppingcart_mvp.model.biz.ShoppingCartBiz;
import com.chenyong.jeff.shoppingcart_mvp.presenter.ShoppingCartPresenter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ShoppingCartPresenterTest {

    private ShoppingCartPresenter shoppingCartPresenter;
    @Mock
    private InterfaceContract.IShoppingCartView iShoppingCartView;
    @Mock
    private ShoppingCartBiz shoppingCartBiz;

    private List<StoreInfo> groups = new ArrayList<>();
    private Map<String, List<GoodsInfo>> children = new HashMap<>();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        shoppingCartPresenter = new ShoppingCartPresenter(iShoppingCartView, shoppingCartBiz);
        fakeData();
        shoppingCartPresenter.initGroups();
        shoppingCartPresenter.initChildren();

    }


    private void fakeData() {
        for (int i = 0; i < 3; i++) {
            groups.add(new StoreInfo(i + "", "天猫店铺" + (i + 1) + "号店"));
            List<GoodsInfo> products = new ArrayList<>();
            for (int j = 0; j <= i; j++) {
                int[] img = {R.drawable.goods1, R.drawable.goods2, R.drawable.goods3, R.drawable.goods4, R.drawable.goods5, R.drawable.goods6};
                if (j % 2 == 0)
                    products.add(new GoodsInfo(j + "", groups.get(i)
                            .getName() + "的第" + (j + 1) + "个商品", 10, 1, img[i * j], true));
                else
                    products.add(new GoodsInfo(j + "", groups.get(i)
                            .getName() + "的第" + (j + 1) + "个商品", 10, 1, img[i * j], false));
            }
            children.put(groups.get(i).getId(), products);// 将组元素的一个唯一值，这里取Id，作为子元素List的Key
        }
    }

    @Test
    public void testShowTotalPrice() throws Exception {
        shoppingCartPresenter.showTotalPrice(groups, children);
        verify(iShoppingCartView).showTotalPriceText(40);
//        assertTrue(shoppingCartPresenter.totalPrice == 40);
    }

    @Test
    public void testChangeCount() throws Exception {
        String groupID = "0";
        String childrenID = "0";
        int count = 2;
        shoppingCartPresenter.changeCount(groupID,childrenID,count);

    }

    @Test
    public void testDeleteGoodsInfo() throws Exception {

    }

    @Test
    public void testCreateOrder() throws Exception {

    }


}