import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.api.windowing.time.Time;


public abstract class ProcessingTimeTrailingBoundedOutOfOrdernessTimestampExtractor<T> implements AssignerWithPeriodicWatermarks<T> {
    private static final long serialVersionUID = 1L;

    private final long maxOutOfOrderness;
    private final long idlenessDetectionDuration;
    private final long processingTimeTrailingDuration;

    private long currentMaxTimestamp;
    private long lastEmittedWatermark = Long.MIN_VALUE;
    private long lastUpdatedTimestamp = Long.MAX_VALUE;

    public ProcessingTimeTrailingBoundedOutOfOrdernessTimestampExtractor(
            Time maxOutOfOrderness,
            Time idlenessDetectionDuration,
            Time processingTimeTrailingDuration) {
        this.maxOutOfOrderness = maxOutOfOrderness.toMilliseconds();
        this.idlenessDetectionDuration = idlenessDetectionDuration.toMilliseconds();
        this.processingTimeTrailingDuration = processingTimeTrailingDuration.toMilliseconds();
        this.currentMaxTimestamp = Long.MIN_VALUE + this.maxOutOfOrderness;
    }

    public abstract long extractTimestamp(T element);

    @Override
    public final Watermark getCurrentWatermark() {
        long currentTimeMillis = System.currentTimeMillis();

        if (lastUpdatedTimestamp == Long.MAX_VALUE) {
            lastUpdatedTimestamp = currentTimeMillis;
        }

        long potentialWM = currentMaxTimestamp - maxOutOfOrderness;

        if (potentialWM > lastEmittedWatermark) {
            lastEmittedWatermark = potentialWM;
        } else if (currentTimeMillis - lastUpdatedTimestamp > idlenessDetectionDuration) {
            long processingTimeWatermark = currentTimeMillis - processingTimeTrailingDuration;
            if (processingTimeWatermark > lastEmittedWatermark) {
                lastEmittedWatermark = processingTimeWatermark;
            }
        }

        return new Watermark(lastEmittedWatermark);
    }

    @Override
    public final long extractTimestamp(T element, long previousElementTimestamp) {
        long timestamp = extractTimestamp(element);
        if (timestamp > currentMaxTimestamp) {
            currentMaxTimestamp = timestamp;
        }
        lastUpdatedTimestamp = System.currentTimeMillis();
        return timestamp;
    }
}
