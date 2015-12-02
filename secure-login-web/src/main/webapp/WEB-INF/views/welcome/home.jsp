<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Home</title>
<link rel="stylesheet"
	href="${f:h(pageContext.request.contextPath)}/resources/app/css/styles.css">
</head>
<body>
	<div id="wrapper">
		<span id="expiredMessage">
			<t:messagesPanel />
		</span>
		<h1>Hello world!</h1>

		<p>Welcome ${f:h(account.firstName)} ${f:h(account.lastName)}</p>

		<c:if test="${lastLoginDate != null}">
			<p id="lastLogin">
				Last login date is ${f:h(lastLoginDate)}.
			</p>
		</c:if>

		<form:form action="${f:h(pageContext.request.contextPath)}/logout">
			<button id="logout">Logout</button>
		</form:form>

		<form:form action="${f:h(pageContext.request.contextPath)}/account">
			<button id="info">Account Information</button>
		</form:form>

		<sec:authorize url="/unlock">
			<form:form
				action="${f:h(pageContext.request.contextPath)}/unlock?form">
				<button id="unlock">Unlock Account</button>
			</form:form>
		</sec:authorize>
	</div>
</body>
</html>
