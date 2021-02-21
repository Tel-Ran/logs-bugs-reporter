package telran.logs.bugs.repo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;

import reactor.core.publisher.Flux;
import telran.logs.bugs.dto.ArtifactCount;
import telran.logs.bugs.dto.LogType;
import telran.logs.bugs.dto.LogTypeCount;
import telran.logs.bugs.mongo.doc.LogDoc;

public class LogStatisticsImpl implements LogStatistics {
private static final String COUNT = "count";
@Autowired
	ReactiveMongoTemplate mongoTemplate;
	@Override
	public Flux<LogTypeCount> getLogTypeCounts() {
		String groupField = LogDoc.LOG_TYPE;
		String outputField = LogTypeCount.LOG_TYPE;
		TypedAggregation<LogDoc> pipeline = getGroupPipeline(groupField, outputField, new ArrayList<>());
		
		return mongoTemplate.aggregate(pipeline, LogTypeCount.class);
		
	}
	@Override
	public Flux<ArtifactCount> getArtifactCounts() {
		TypedAggregation<LogDoc> pipeline = getGroupPipeline(LogDoc.ARTIFACT, ArtifactCount.ARTIFACT, new ArrayList<>());
		return mongoTemplate.aggregate(pipeline, ArtifactCount.class);
	}

	private TypedAggregation<LogDoc> getGroupPipeline(String groupField, String outputField, List<AggregationOperation> aggregationOperations
			) {
		fillListOperations(groupField, outputField, aggregationOperations);
		TypedAggregation<LogDoc> pipeline =
				Aggregation.newAggregation(LogDoc.class, aggregationOperations);
		return pipeline;
	}
	private void fillListOperations(String groupField, String outputField,
			List<AggregationOperation> aggregationOperations) {
		GroupOperation groupOperation = Aggregation.group(groupField).count().as(COUNT);
		ProjectionOperation projOperation = Aggregation.project(COUNT).and("_id")
				.as(outputField);
		SortOperation sortOperation = Aggregation.sort(Direction.DESC, COUNT);
		aggregationOperations.add(groupOperation);
		aggregationOperations.add(sortOperation);
		aggregationOperations.add(projOperation);
	}
	@Override
	public Flux<ArtifactCount> getMostEncounteredArtifacts(int nArtifacts) {
		
		TypedAggregation<LogDoc> pipeline = getGroupPipeline(LogDoc.ARTIFACT, ArtifactCount.ARTIFACT, new ArrayList<>(), nArtifacts);
		
		return mongoTemplate.aggregate(pipeline, ArtifactCount.class);
	}
	private TypedAggregation<LogDoc> getGroupPipeline(String groupField, String outputField,
			ArrayList<AggregationOperation> aggregationOperations, int limit) {
		fillListOperations(groupField, outputField, aggregationOperations);
		aggregationOperations.add(Aggregation.limit(limit));
		TypedAggregation<LogDoc> pipeline =
				Aggregation.newAggregation(LogDoc.class, aggregationOperations);
		return pipeline;
		
	}
	@Override
	public Flux<LogTypeCount> getMostEncounteredExceptions(int nExceptions) {
		ArrayList<AggregationOperation> aggregationOperations = new ArrayList<>();
		aggregationOperations.add(Aggregation.match(Criteria.where(LogDoc.LOG_TYPE)
				.ne(LogType.NO_EXCEPTION)));
		TypedAggregation<LogDoc> pipline =
				getGroupPipeline(LogDoc.LOG_TYPE, LogTypeCount.LOG_TYPE, aggregationOperations,
						nExceptions);
		return mongoTemplate.aggregate(pipline, LogTypeCount.class);
	}
	
}
