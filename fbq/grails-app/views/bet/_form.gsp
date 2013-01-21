<%@ page import="org.Bet" %>



<div class="fieldcontain ${hasErrors(bean: betInstance, field: 'aid', 'error')} ">
	<label for="aid">
		<g:message code="bet.aid.label" default="Aid" />
		
	</label>
	<g:textField name="aid" value="${betInstance?.aid}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: betInstance, field: 'bet', 'error')} required">
	<label for="bet">
		<g:message code="bet.bet.label" default="Bet" />
		<span class="required-indicator">*</span>
	</label>
	<g:field type="number" name="bet" required="" value="${fieldValue(bean: betInstance, field: 'bet')}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: betInstance, field: 'betOn', 'error')} required">
	<label for="betOn">
		<g:message code="bet.betOn.label" default="Bet On" />
		<span class="required-indicator">*</span>
	</label>
	<g:field type="number" name="betOn" required="" value="${fieldValue(bean: betInstance, field: 'betOn')}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: betInstance, field: 'cid', 'error')} ">
	<label for="cid">
		<g:message code="bet.cid.label" default="Cid" />
		
	</label>
	<g:textField name="cid" value="${betInstance?.cid}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: betInstance, field: 'clientId', 'error')} ">
	<label for="clientId">
		<g:message code="bet.clientId.label" default="Client Id" />
		
	</label>
	<g:textField name="clientId" value="${betInstance?.clientId}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: betInstance, field: 'matchId', 'error')} ">
	<label for="matchId">
		<g:message code="bet.matchId.label" default="Match Id" />
		
	</label>
	<g:textField name="matchId" value="${betInstance?.matchId}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: betInstance, field: 'matchTime', 'error')} required">
	<label for="matchTime">
		<g:message code="bet.matchTime.label" default="Match Time" />
		<span class="required-indicator">*</span>
	</label>
	<g:datePicker name="matchTime" precision="day"  value="${betInstance?.matchTime}"  />
</div>

