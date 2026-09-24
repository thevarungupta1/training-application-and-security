from abc import ABC, abstractmethod

# define an abstract class for payment
class PaymentProcess(ABC):
    @abstractmethod
    def authorize(self, amount: float) -> bool:
        """Authorize the payment for the given amount"""
        pass
    
    @abstractmethod
    def capture(self, amount: float) -> bool:
        """Capture the payment for the given amount"""
        pass
    
    def process(self, amount: float) -> None:
        """Process the payment by authorizing and then capturing the amount"""
        if self.authorize(amount):
            self.capture(amount)
        else:
            print("Authorization failed.")
            
# define a subclass for credit card payment
class StripProcessor(PaymentProcess):
    def authorize(self, amount: float) -> bool:
        print(f"Authorizing credit card payment for amount: {amount}")
        return True

    def capture(self, amount: float) -> bool:
        print(f"Capturing credit card payment for amount: {amount}")
        return True
    
class PayPalProcessor(PaymentProcess):
    def authorize(self, amount: float) -> bool:
        print(f"Authorizing PayPal payment for amount: {amount}")
        return True

    def capture(self, amount: float) -> bool:
        print(f"Capturing PayPal payment for amount: {amount}")
        return True
    
# client code does not depend on concrete implementations
def checkout(amount: float, processor: PaymentProcess) -> None:
    processor.process(amount)
    

stripe = StripProcessor()
paypal = PayPalProcessor()

checkout(100.0, stripe)
checkout(200.0, paypal)