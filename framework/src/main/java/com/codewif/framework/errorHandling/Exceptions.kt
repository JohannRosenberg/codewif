package com.codewif.framework.errorHandling

class TestAlreadyAddedException(details: String) : Exception(details)
class NoTestDefinedException(details: String) : Exception(details)
class MissingSetupDataException(details: String) : Exception(details)

