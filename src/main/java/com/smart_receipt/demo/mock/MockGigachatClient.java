package com.smart_receipt.demo.mock;

import com.smart_receipt.dto.ai.AiReplyDto;
import com.smart_receipt.dto.ai.GigachatReplyDto;
import com.smart_receipt.dto.ai.GigachatReplyMessageDto;
import com.smart_receipt.feign.FeignToGigachatClient;
import com.smart_receipt.request.GigachatRequest;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("demo")
public class MockGigachatClient implements FeignToGigachatClient {

    @Override
    public AiReplyDto getAiReply(GigachatRequest request, String authorization) {
        String mockedResponse = """
                {
                  "products": [
                    {
                      "name": "Картофель молодой",
                      "price": 35.9,
                      "categoryName": "Овощи и фрукты"
                    },
                    {
                      "name": "Морковь",
                      "price": 64.15,
                      "categoryName": "Овощи и фрукты"
                    },
                    {
                      "name": "Огурцы грунтовые",
                      "price": 61.45,
                      "categoryName": "Овощи и фрукты"
                    },
                    {
                      "name": "Филе бедра индейки Пава Пава",
                      "price": 306.43,
                      "categoryName": "Мясо, птица, рыба"
                    },
                    {
                      "name": "Улебцы THA! хрустящие Кукурузные",
                      "price": 14.99,
                      "categoryName": "Хлебобулочные изделия"
                    },
                    {
                      "name": "Улебцы TYH! хрустящие Кукурузные",
                      "price": 14.99,
                      "categoryName": "Хлебобулочные изделия"
                    },
                    {
                      "name": "Пирожное заварное Смольнинское",
                      "price": 138.99,
                      "categoryName": "Сладости, кондитерский"
                    },
                    {
                      "name": "Свекла",
                      "price": 54.53,
                      "categoryName": "Овощи и фрукты"
                    },
                    {
                      "name": "Хлебцы THR! хрустящие Кукурузные",
                      "price": 14.99,
                      "categoryName": "Хлебобулочные изделия"
                    },
                    {
                      "name": "Укроп",
                      "price": 29.99,
                      "categoryName": "Другое"
                    },
                    {
                      "name": "Томаты Черри желтые на ветке",
                      "price": 79.99,
                      "categoryName": "Овощи и фрукты"
                    },
                    {
                      "name": "Улитка греческая рулетик с маком",
                      "price": 52.78,
                      "categoryName": "Другое"
                    },
                    {
                      "name": "Корок свиной Дикий Кабан",
                      "price": 340.94,
                      "categoryName": "Мясо, птица, рыба"
                    },
                    {
                      "name": "Сосиски Молочные W/o",
                      "price": 142.55,
                      "categoryName": "Мясо, птица, рыба"
                    },
                    {
                      "name": "Закусочка Дядя Ваня Стокгольмская",
                      "price": 109.99,
                      "categoryName": "Другое"
                    },
                    {
                      "name": "Релив Дядя Ваня с огурцами и горчицей",
                      "price": 150.00,
                      "categoryName": "Другое"
                    },
                    {
                      "name": "Килька балтийская TSH!",
                      "price": 64.99,
                      "categoryName": "Другое"
                    },
                    {
                      "name": "Сыр из смешанного молока",
                      "price": 256.66,
                      "categoryName": "Молочные продукты"
                    },
                    {
                      "name": "ВЗИ Сметана Простоквашино",
                      "price": 44.99,
                      "categoryName": "Молочные продукты"
                    },
                    {
                      "name": "Улебцы ТЧН! хрустящие Кукурузные",
                      "price": 14.99,
                      "categoryName": "Хлебобулочные изделия"
                    }
                  ]
                }]]]]
                """;

        return AiReplyDto
                .builder()
                .choices(List.of(
                        GigachatReplyDto
                                .builder()
                                .message(
                                        GigachatReplyMessageDto
                                                .builder()
                                                .role("assistant")
                                                .content(mockedResponse)
                                                .build())
                                .build()
                ))
                .build();
    }
}

