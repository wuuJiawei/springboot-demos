package xyz.easyboot.handler.state;

import com.alibaba.cola.statemachine.StateMachine;
import com.alibaba.cola.statemachine.builder.StateMachineBuilder;
import com.alibaba.cola.statemachine.builder.StateMachineBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.easyboot.handler.state.order.OrderContext;
import xyz.easyboot.handler.state.order.OrderEvent;
import xyz.easyboot.handler.state.order.OrderState;

@Configuration
public class StateMachineConfiguration {

    @Bean
    public StateMachine<OrderState, OrderEvent, OrderContext> orderStateMachine() {
        StateMachineBuilder<OrderState, OrderEvent, OrderContext> builder = StateMachineBuilderFactory.create();

        builder.externalTransition()
                .from(OrderState.READY)
                .to(OrderState.READY);
//                .on(OrderState.READY)
//                .when()
//                .perform();
        return builder.build("");
    }

}
