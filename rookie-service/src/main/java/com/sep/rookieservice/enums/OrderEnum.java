package com.sep.rookieservice.enums;

public enum OrderEnum {
    UNORDERED((byte) 0),   // Đã thêm vào giỏ hàng nhưng chưa đặt hàng
    PENDING((byte) 1),     // Người dùng đã nhấn "Đặt hàng", chờ shop xác nhận
    PROCESSING((byte) 2),  // Shop đã xác nhận, đơn hàng đang được chuẩn bị
    SHIPPING((byte) 3),    // Đơn hàng đang được vận chuyển
    DELIVERED((byte) 4),   // Đơn hàng đã giao thành công
    CANCELLED((byte) 5),   // Đơn hàng bị hủy trước giao và sau giao
    RETURNED((byte) 6);    // Khách hàng trả lại đơn hàng
    private final byte order_status;

    OrderEnum(byte order_status) {
        this.order_status = order_status;
    }

    public byte getStatus() {
        return order_status;
    }
    public static OrderEnum getByStatus(byte order_status) {
        for (OrderEnum order : OrderEnum.values()) {
            if (order.getStatus() == order_status) {
                return order;
            }
        }
        throw new IllegalArgumentException("Invalid order status: " + order_status);
    }
}
