package com.example.demo.Service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Title :
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Author :Hairui
 * Date :2018/4/18
 * Time :13:58
 * </p>
 * <p>
 * Department :
 * </p>
 * <p> Copyright : 江苏飞博软件股份有限公司 </p>
 */
@Service
public class SecondKill {
    public static Map<String,Integer> map = new HashMap<>();
    private List<Integer> killA = new ArrayList<>();
    private List<Integer> killB = new ArrayList<>();
    static {
        map.put("a", 10000);
        map.put("b", 10000);
    }
    public /*synchronized*/ void reduce(String commodityId,int userId){
        if(map.get(commodityId)>9700){
            map.put(commodityId, map.get(commodityId) - 1);
            if("a".equals(commodityId)){
                killA.add(userId);
            }
            else{
                killB.add(userId);
            }
        }
        else{
            //message to user:sorry you lose about the kill;
        }
    }

    public List<Integer> getKillA() {
        return killA;
    }

    public List<Integer> getKillB() {
        return killB;
    }
}
