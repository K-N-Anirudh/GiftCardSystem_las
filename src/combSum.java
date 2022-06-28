import com.sun.org.apache.xalan.internal.res.XSLTErrorResources_zh_CN;

import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

public class combSum {
    static ArrayList<ArrayList<Integer>> res = new ArrayList<>();
    static Deque<Integer> temp = new ArrayDeque<>();
    static void helper(ArrayList<Integer> src,int index,int curSum,int target,Deque<Integer> temp){
        if(curSum > target){
            return;
        }
        if(index>=src.size()){
            System.out.println(temp + " "+curSum);
            if(curSum == target){
                res.add(new ArrayList<>(temp));
                return;
            }
            return;
        }

        if(curSum == target){
            res.add(new ArrayList<>(temp));
            return;
        }
        temp.add(src.get(index));
        curSum += src.get(index);
        helper(src,index+1,curSum,target,temp);
        curSum -= temp.pop();
        helper(src,index+1,curSum,target,temp);

    }
    public static void main(String[] args) {
       ArrayList<Integer> src = new ArrayList<>();
       src.add(3);
       src.add(1);
       src.add(2);       src.add(4);

        src.add(6);

       helper(src,0,0,6,temp);
        System.out.println(res);
    }
}
