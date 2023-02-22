package kr.songjava.web.thymeleaf;

import java.util.HashSet;
import java.util.Set;

import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;

public class HtmlTagDialect extends AbstractProcessorDialect {

	public HtmlTagDialect() {
		super("HtmlTagDialect", "tag", 1000);
	}

	public Set<IProcessor> getProcessors(final String dialectPrefix) {
		final Set<IProcessor> processors = new HashSet<IProcessor>();
		processors.add(new ReplaceLineAttributeTagProcessor(dialectPrefix));
		return processors;
	}

}