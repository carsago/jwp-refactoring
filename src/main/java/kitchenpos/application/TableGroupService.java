package kitchenpos.application;

import static java.util.stream.Collectors.toList;

import java.util.List;
import kitchenpos.application.request.OrderTableDto;
import kitchenpos.application.request.TableGroupCreateRequest;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.TableGroup;
import kitchenpos.persistence.OrderRepository;
import kitchenpos.persistence.OrderTableRepository;
import kitchenpos.persistence.TableGroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TableGroupService {

    private final OrderRepository orderRepository;
    private final OrderTableRepository orderTableRepository;
    private final TableGroupRepository tableGroupRepository;


    public TableGroupService(OrderRepository orderRepository, OrderTableRepository orderTableRepository,
                             TableGroupRepository tableGroupRepository) {
        this.orderRepository = orderRepository;
        this.orderTableRepository = orderTableRepository;
        this.tableGroupRepository = tableGroupRepository;
    }

    @Transactional
    public TableGroup create(TableGroupCreateRequest request) {
        List<Long> orderTableIds = request.getOrderTables().stream()
            .map(OrderTableDto::getId)
            .collect(toList());
        List<OrderTable> savedOrderTables = getOrderTables(orderTableIds);
        return getTableGroup(savedOrderTables);
    }

    @Transactional
    public void ungroup(Long tableGroupId) {
        List<OrderTable> orderTables = orderTableRepository.findAllByTableGroupId(tableGroupId);
        validateUngroupable(orderTables);
        orderTables.forEach(OrderTable::unGroup);
    }

    private List<OrderTable> getOrderTables(List<Long> orderTableIds) {
        List<OrderTable> savedOrderTables = orderTableRepository.findAllByIdIn(orderTableIds);
        if (orderTableIds.size() != savedOrderTables.size()) {
            throw new IllegalArgumentException();
        }
        return savedOrderTables;
    }

    private TableGroup getTableGroup(List<OrderTable> savedOrderTables) {
        TableGroup tableGroup = tableGroupRepository.save(TableGroup.createEmpty());
        tableGroup.group(savedOrderTables);
        return tableGroup;
    }

    private void validateUngroupable(List<OrderTable> orderTables) {
        List<Long> orderTableIds = orderTables.stream()
            .map(OrderTable::getId)
            .collect(toList());
        if (orderRepository.existsByOrderTableIdInAndOrderStatusIn(
            orderTableIds, List.of(OrderStatus.COOKING, OrderStatus.MEAL))) {
            throw new IllegalArgumentException();
        }
    }
}
