package com.chenyong.jeff.shoppingcart_mvp.TasksDataSource;

/**
 *jian ting
 * Created by Administrator on 2016/9/9.
 */
public interface LoadTasksCallback<T> {
    void onTasksLoaded(T t);

    void onDataNotAvailable(String message);
}
