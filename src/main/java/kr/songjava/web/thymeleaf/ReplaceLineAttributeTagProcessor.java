package kr.songjava.web.thymeleaf;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.FragmentExpression;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressionExecutionContext;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templatemode.TemplateMode;

public class ReplaceLineAttributeTagProcessor extends AbstractAttributeTagProcessor {

	private static final String ATTR_NAME = "replaceLine";
	private static final int PRECEDENCE = 10000;

	public ReplaceLineAttributeTagProcessor(final String dialectPrefix) {
		super(TemplateMode.HTML, dialectPrefix, null, false, ATTR_NAME, true, PRECEDENCE, true);
	}

	@Override
	protected void doProcess(final ITemplateContext context, final IProcessableElementTag tag,
			final AttributeName attributeName, final String attributeValue,
			final IElementTagStructureHandler handler) {
		final IEngineConfiguration configuration = context.getConfiguration();
		final IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(configuration);
		final IStandardExpression expression = expressionParser.parseExpression(context, attributeValue);
		final Object expressionResult;
		if (expression != null && expression instanceof FragmentExpression) {
			final FragmentExpression.ExecutedFragmentExpression executedFragmentExpression = FragmentExpression
					.createExecutedFragmentExpression(context, (FragmentExpression) expression);
			expressionResult = FragmentExpression.resolveExecutedFragmentExpression(context, executedFragmentExpression,
					true);
		} else {
			expressionResult = expression.execute(context, StandardExpressionExecutionContext.RESTRICTED);
		}
		final String result = (expressionResult == null ? "" : expressionResult.toString());
		final String value = result.replace("\n", "<br/>");
		handler.setBody(value, false);
	}
}