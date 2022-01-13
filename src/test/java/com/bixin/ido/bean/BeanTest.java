package com.bixin.ido.bean;

import com.bixin.common.utils.BeanReflectUtil;
import com.bixin.common.utils.JacksonUtil;
import com.bixin.nft.bean.DO.NftCompositeCard;
import com.bixin.nft.bean.bo.CompositeCardBean;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author zhangcheng
 * create  2022/1/12
 */
public class BeanTest {

    static final Map<String, String> map = new HashMap<>() {{
        put("Facial Expression", "expression");
    }};

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        of(null);
//
//        NftCompositeCard card = NftCompositeCard.builder()
//                .accessoriesId(1111L)
//                .leftHandId(2222L)
//                .tailId(3333L)
//                .build();
//        Class clazz = card.getClass();
//        Field[] fs = clazz.getDeclaredFields();
//        for (Field field : fs) {
//
//            field.setAccessible(true);
////            System.out.println(field.get(user));
//            /** 先获取变量名 **/
//            String fieldName = field.getName();
//            if ("serialVersionUID".equalsIgnoreCase(fieldName)) {
//                continue;
//            }
//            /**拼接get方法,如getId  **/
//            String getMethod = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1, fieldName.length());
//            /**
//             调用clazz的getMethod方法获取类的指定get方法
//             通过invoke执行获取变量值
//             **/
//            System.out.println(clazz.getMethod(getMethod).invoke(card));
//        }
        List<CompositeCardBean.CustomCardElement> elements = Arrays.asList(
                CompositeCardBean.CustomCardElement.builder()
                        .eleName("Left Hand")
                        .id(11111L)
                        .build(),
                CompositeCardBean.CustomCardElement.builder()
                        .eleName("Facial Expression")
                        .id(22222L)
                        .build(),
                CompositeCardBean.CustomCardElement.builder()
                        .eleName("Tail")
                        .id(33333L)
                        .build()
        );
        NftCompositeCard card = new NftCompositeCard();
        for (CompositeCardBean.CustomCardElement element : elements) {
            String eleName = element.getEleName();
            if (Objects.nonNull(map.get(eleName))) {
                eleName = map.get(eleName);
            }
            String name = eleName.replaceAll("\\s*", "");
            String fieldName = name.substring(0, 1).toLowerCase()
                    + name.substring(1, name.length())
                    + "Id";
            BeanReflectUtil.setFieldValue(card, fieldName, element.getId());
        }
        System.out.println(card);

        System.out.println("------------");

        Map<Integer, NftCompositeCard> map = new HashMap<>();
        map.put(0, card);
        map.put(1, card);
        System.out.println("map:: "+ JacksonUtil.toJson(map));


        String value = "{'status': 'success', 'message': '上传成功', 'url': 'https://imagedelivery.net/3mRLd_IbBrrQFSP57PNsVw/19b5cb51-9cf3-4cbf-1c03-a6ee0cec2900/public'}";
        String s = value.replaceAll("'", "\"");
        System.out.println(s);
        Map<String,Object> map1 = JacksonUtil.readValue(s, Map.class);
        System.out.println(map1);
    }

}
