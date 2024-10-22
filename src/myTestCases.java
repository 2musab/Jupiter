import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class myTestCases {

	WebDriver driver = new ChromeDriver();

	@BeforeTest
	public void mySetup() {

		driver.get("https://jupiter.cloud.planittesting.com/#/shop");

	}

	@Test(priority = 1, enabled = false)
	public void addAllItemsHomePage() {

		WebElement content = driver.findElement(By.cssSelector("div[class='products ng-scope'] ul"));
		List<WebElement> contentList = content.findElements(By.tagName("li"));

		for (int i = 0; i < contentList.size(); i++) {

			WebElement button = contentList.get(i).findElement(By.cssSelector(".btn.btn-success"));
			button.click();
		}

	}

	@Test(priority = 2, enabled = false)
	public void higherAmount() {

		WebElement content = driver.findElement(By.cssSelector("div[class='products ng-scope'] ul"));
		List<WebElement> contentList = content.findElements(By.tagName("li"));

		// Variables to store the highest price item details
		String highestPricedItem = null;
		double highestPrice = 0.0;
		int highestPriceIndex = -1; // Track the index of the highest price item

		// Loop through the content list and extract titles and prices
		for (int i = 0; i < contentList.size(); i++) {
			// Get item title
			String title = contentList.get(i).findElement(By.cssSelector(".product-title.ng-binding")).getText();

			// Get item price
			String price = contentList.get(i).findElement(By.cssSelector(".product-price.ng-binding")).getText();
			price = price.replaceAll("[^\\d.]", ""); // Remove non-numeric characters
			double priceInDouble = Double.parseDouble(price); // Convert to double

			// Check if this item has the highest price so far
			if (priceInDouble > highestPrice) {
				highestPrice = priceInDouble;
				highestPricedItem = title;
				highestPriceIndex = i; // Save the index of this item
			}
		}

		// Output the highest priced item and its price
		System.out.println("The highest priced item is: " + highestPricedItem + " with a price of: $" + highestPrice);

		// Add the highest priced item to the cart
		if (highestPriceIndex != -1) {
			contentList.get(highestPriceIndex).findElement(By.cssSelector(".btn.btn-success")).click(); // Click the
																										// "Add to
																										// Cart"
																										// button
			System.out.println(highestPricedItem + " has been added to the cart.");
		} else {
			System.out.println("No items were found.");
		}
	}

	@Test(priority = 3, enabled = true)
	public void addSecondRowItems() {

		WebElement content = driver.findElement(By.cssSelector("div[class='products ng-scope'] ul"));
		List<WebElement> contentList = content.findElements(By.tagName("li"));

		for (int i = 3; i < contentList.size() - 2; i++) {

			WebElement button = contentList.get(i).findElement(By.cssSelector(".btn.btn-success"));
			button.click();
		}

	}

	@Test(priority = 4, enabled = true)
	public void goToCart() throws InterruptedException {

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

		// Navigate to cart
		WebElement cart = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href='#/cart']")));
		cart.click();

		// Wait for the cart items table to load
		WebElement table = wait.until(
				ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".table.table-striped.cart-items")));
		System.out.println(table.getText());

		// Handle stale elements with retries
		interactWithCartItem(wait, 0); // First cart item
		interactWithCartItem(wait, 1); // Second cart item
	}

	// Helper method to interact with a cart item and handle stale elements
	private void interactWithCartItem(WebDriverWait wait, int itemIndex) {
		int attempts = 3; // Retry limit for stale elements
		while (attempts > 0) {
			try {
				// Refresh the table and cart item list before interaction
				WebElement table = wait.until(ExpectedConditions
						.visibilityOfElementLocated(By.cssSelector(".table.table-striped.cart-items")));
				List<WebElement> tableList = table.findElements(By.cssSelector(".cart-item.ng-scope"));

				// Click the 'ng-confirm' button on the specific item
				WebElement confirmButton = tableList.get(itemIndex).findElement(By.tagName("ng-confirm"));
				confirmButton.click();

				// Wait for the 'Yes' button and click it
				WebElement yesButton = wait
						.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[class='btn btn-success']")));
				yesButton.click();

				break; // Exit the loop if no exception occurs
			} catch (org.openqa.selenium.StaleElementReferenceException e) {
				System.out.println("StaleElementReferenceException caught. Retrying...");
				attempts--; // Decrement attempts and retry
			}
		}
	}
}