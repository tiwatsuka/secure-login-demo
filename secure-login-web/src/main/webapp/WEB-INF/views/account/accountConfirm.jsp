<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8" />
<title>Create Account</title>
<link rel="stylesheet"
	href="${f:h(pageContext.request.contextPath)}/resources/app/css/styles.css" />
</head>
<body>
	<div id="wrapper">
		<h1>Create Account</h1>
		<form:form
			action="${f:h(pageContext.request.contextPath)}/account/create" method="POST"
			modelAttribute="accountCreateForm">
			<table>
				<tr>
					<th>Username</th>
					<td>${f:h(accountCreateForm.username)}</td>
					<form:hidden path="username"/>
				</tr>
				<tr>
					<th>First name</th>
					<td>${f:h(accountCreateForm.firstName)}</td>
					<form:hidden path="firstName"/>
				</tr>
				<tr>
					<th>Last name</th>
					<td>${f:h(accountCreateForm.lastName)}</td>
					<form:hidden path="lastName"/>
				</tr>
				<tr>
					<th>E-mail</th>
					<td>${f:h(accountCreateForm.email)}</td>
					<form:hidden path="email"/>
				</tr>
					<form:hidden path="confirmEmail"/>
				<tr>
					<th>URL</th>
					<td>${f:h(accountCreateForm.url)}</td>
					<form:hidden path="url"/>
				</tr>
				<tr>
					<th>Image</th>
					<td>${f:h(accountCreateForm.image.originalFilename)}</td>
				</tr>
				<form:hidden path="imageId"/>
			</table>
			<input type="submit" name="redo" value="Back" />
			<input type="submit" id="create" value="Create new account"/>
		</form:form>
	</div>
</body>
</html>