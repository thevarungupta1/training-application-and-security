class Bank:
    def getInterestRate(self):
        return 5.0  # default interest rate for the bank
    
class A(Bank):
    def getInterestRate(self):
        return 7.0  # interest rate for Bank A

class B(Bank):
    def getInterestRate(self):
        return 6.5  # interest rate for Bank B
    
bankA = A()
bankB = B()

print(bankA.getInterestRate())  # 7.0
print(bankB.getInterestRate())  # 6.5

bankDefault = Bank()
print(bankDefault.getInterestRate())  # 5.0