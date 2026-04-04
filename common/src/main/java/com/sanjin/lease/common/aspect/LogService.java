package com.sanjin.lease.common.aspect;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LogService {

    @Async // 核心：标记为异步执行，会由 Spring 的线程池来调用
    public void saveLog(String methodName, long duration, Object result, Object[] args) {
        // 模拟耗时操作，比如写入数据库或发送到远程日志系统
        log.info("【异步日志】方法：{}, 耗时：{}ms, 结果：{}", methodName, duration, result);

        // 使用反射通用方式提取业务参数（避免 common 模块依赖具体业务 DTO）
        if (args != null && args.length > 0) {
            for (Object arg : args) {
                if (arg != null) {
                    try {
                        // 尝试通过反射获取 userId 和 roomId（如果对象有这些 getter 方法）
                        String className = arg.getClass().getSimpleName();
                        
                        // 检查是否是秒杀相关的类
                        if (className.contains("Seckill") || className.contains("Room")) {
                            // 尝试反射获取 userId
                            Long userId = null;
                            Long roomId = null;
                            
                            try {
                                java.lang.reflect.Method getUserIdMethod = arg.getClass().getMethod("getUserId");
                                userId = (Long) getUserIdMethod.invoke(arg);
                            } catch (Exception e) {
                                // 忽略，说明没有 userId 字段
                            }
                            
                            try {
                                java.lang.reflect.Method getRoomIdMethod = arg.getClass().getMethod("getRoomId");
                                roomId = (Long) getRoomIdMethod.invoke(arg);
                            } catch (Exception e) {
                                // 忽略，说明没有 roomId 字段
                            }
                            
                            if (userId != null || roomId != null) {
                                log.info("【秒杀审计】状态：COMPLETED, 用户：{}, 房源：{}, 耗时：{}ms, 响应：{}",
                                        userId, roomId, duration, result);
                                break;
                            }
                        }
                    } catch (Exception e) {
                        // 反射失败时不处理，避免影响主流程
                        log.debug("反射提取参数失败：{}", e.getMessage());
                    }
                }
            }
        }
    }
}
