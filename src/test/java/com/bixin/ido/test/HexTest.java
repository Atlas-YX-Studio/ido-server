package com.bixin.ido.test;

import com.bixin.common.utils.HexStringUtil;

/**
 * @author zhangcheng
 * create   2021/9/8
 */
public class HexTest {

    /*
   EventNotificationResult{
          blockHash='0xceeff0a727b5e63e1080a92b331f642bc3a486cd4d8b586662abc49ccac13227',
                  blockNumber='1811675',
                  transactionHash='0xe80bc3f2d52ea9c3f47081d6fd0c6e7c0bb65373b7cd4daa9729140d38e98a12'
          transactionIndex=1,
                  data='{
          "signer":"0x3cf910d97c98947277be21d6b7625cfe",
                  "x_token_code":{
              "addr":"0x5b876a58b0e1cff855b6489cd8cf3bec",
                      "module_name":"0x44756d6d79546f6b656e",
                      "name":"0x454f53"
          },
          "y_token_code":{
              "addr":"0x5b876a58b0e1cff855b6489cd8cf3bec",
                      "module_name":"0x44756d6d79546f6b656e",
                      "name":"0x55534454"
          },
          "amount_x_in":1000000000,
                  "amount_y_in":0,
                  "amount_x_out":0,
                  "amount_y_out":9953298066,
                  "reserve_x":10008900300994,
                  "reserve_y":99911348760492,
                  "block_timestamp_last":1631008584052
      }',
      typeTag='0x3cf910d97c98947277be21d6b7625cfe::SwapPair::SwapEvent',
      eventKey='0x11000000000000003cf910d97c98947277be21d6b7625cfe',
      eventSeqNumber='9'
  }
  */
    public static void main(String[] args) {
//        String hex = "0x454f53";
        String hex = "0x454f53".replaceAll("0x", "");
//        String hex1 = "0x55534454";
        String hex1 = "0x55534454".replaceAll("0x", "");
        String s = HexStringUtil.toStringHex(hex);
        String s1 = HexStringUtil.toStringHex(hex1);

        System.out.println(s);
        System.out.println(s1);
//        System.out.println(HexStringUtil.decode(hex));
        System.out.println("--------------------------");

//        String curl = "curl -X POST -F file=@/Users/bixin/Documents/pics/ss.jpg -H \"Authorization: Bearer 5_crM9D0ZQEjTmJm6P_J9CjAgxU06AKt0ZB-xeAb\" https://api.cloudflare.com/client/v4/accounts/06d48301c855502bf143d5a4c5d3a982/images/v1";
//        String[] cmds = curl.split(" ");
//        String[] cmds = new String[]{"curl", "-X", "POST", "-F", "file=@/Users/bixin/Documents/pics/ss.jpg", "-H", "Authorization: Bearer 5_crM9D0ZQEjTmJm6P_J9CjAgxU06AKt0ZB-xeAb", "https://api.cloudflare.com/client/v4/accounts/06d48301c855502bf143d5a4c5d3a982/images/v1"};
//
//
//        ProcessBuilder process = new ProcessBuilder(cmds);
//        Process p;
//        try {
//            p = process.start();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
//            StringBuilder builder = new StringBuilder();
//            String line;
//            while ((line = reader.readLine()) != null) {
//                builder.append(line);
//            }
//            System.out.println(builder.toString());
//            Map map = JacksonUtil.readValue(builder.toString(), Map.class);
//            boolean success = (boolean) map.get("success");
//            System.out.println("rs:" + success);
//            if(success){
//                Map result = (Map) map.get("result");
//                ArrayList<String> variants = (ArrayList<String>) result.get("variants");
//                System.out.println("path:"+variants.get(0));
//            }
//
//
//        } catch (IOException e) {
//            System.out.println(e);
//        }


    }


}
