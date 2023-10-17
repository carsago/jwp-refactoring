package kitchenpos.application.request;

import kitchenpos.domain.OrderLineItem;

public class OrderLineItemCreateRequest {

    private final Long menuId;
    private final Integer quantity;

    public OrderLineItemCreateRequest(Long menuId, Integer quantity) {
        this.menuId = menuId;
        this.quantity = quantity;
    }

    public Long getMenuId() {
        return menuId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public OrderLineItem toEntity() {
        return new OrderLineItem(menuId, quantity);
    }
}
