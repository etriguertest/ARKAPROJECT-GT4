//package com.arka.movements.aws;
//
//import io.awspring.cloud.sqs.operations.SqsTemplate;
//
//public class main {
//    private final MessageProducerService messageProducerService;
//
//    // Spring
//    public main(MessageProducerService messageProducerService) {
//        this.messageProducerService = messageProducerService;
//    }
//    public static void main(String[] args) {
//        String jsonMovementList = """
//            [
//                {
//                    "inventoryUnitId": 31,
//                    "movementType": "RECEPCION_POR_COMPRA",
//                    "movementDate": "2025-10-18",
//                    "documentReference": "PO-000551",
//                    "idOperator": 101,
//                    "nameOperator": "Alice M.",
//                    "quantityType": "INCREASE",
//                    "quantity": 10,
//                    "fromBranch": 1,
//                    "toBranch":1
//                },
//                {
//                    "inventoryUnitId": 31,
//                    "movementType": "RECEPCION_POR_COMPRA",
//                    "movementDate": "2025-10-18",
//                    "documentReference": "PO-000551",
//                    "idOperator": 101,
//                    "nameOperator": "Alice M.",
//                    "quantityType": "INCREASE",
//                    "quantity": 10,
//                    "fromBranch": 1,
//                    "toBranch":1
//                }
//            ]
//            """;
//
//        messageProducerService.send(jsonMovementList);
//    }
//}
