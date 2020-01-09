import com.alibaba.fastjson.JSON;
import com.apicloud.moduleDemo.JsonUtil;

import org.junit.Test;

public class TestJson {

    @Test
    public void test(){
        String json="{\n" +
                "        \"batchNo\": \"\",\n" +
                "        \"Weight\": \"0.00KG\",\n" +
                "        \"produceName\": \"55# 透析纸 60g\",\n" +
                "        \"width\": \"0.195000M\",\n" +
                "        \"num1\": \"761F467D-6628-496A-9B76-B9E8B8F44DCE\",\n" +
                "        \"location\": \"D28\",\n" +
                "        \"materialNo\": \"Y.3.001.034.000222\",\n" +
                "        \"long\": \"705M\",\n" +
                "        \"type\": \"医包行业_半成品_透析纸_55# 透析纸（台湾）_55# 透析纸 60g\",\n" +
                "        \"isFree\": \"被占用\",\n" +
                "        \"PurchaseOrderNo\": \"\"\n" +
                "      }";
        MaterialModel materialModel = (MaterialModel) JsonUtil.convertJsonToObject(json, MaterialModel.class);
        System.out.println(JSON.toJSONString(materialModel));
    }
}
