package models.trainers;

/**
 * Тип стратегии опроса во время обучения
 */
public enum QueriesRangeType {
    /**
     * Линейная - опросы происходят через равные промежутки
     */
    LINEAR(),

    /**
     * Нелинейная - опросы чаще происходят в начале обучения, промежуток между опросами увеличивается по мере обучения
     */
    NON_LINEAR()
}
