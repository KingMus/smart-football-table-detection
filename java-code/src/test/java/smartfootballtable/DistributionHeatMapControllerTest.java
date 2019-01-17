package smartfootballtable;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class DistributionHeatMapControllerTest {

	@Test
	public void settingOnePositionInHeatMapTile_shouldHaveDistributionOfOneHundredPercent() {

		DistributionHeatMapController distController = new DistributionHeatMapController();

		distController.addPositionToHeatMapTile(1);

		assertThat(distController.getDistributionInPercentOfHeatMapTile(1), is(100.0));
	}

	@Test
	public void settingOnePositionInTwoHeatMapTiles_shouldHaveDistributionOfFiftyPercentInEachHeatMapTile() {

		DistributionHeatMapController distController = new DistributionHeatMapController();

		distController.addPositionToHeatMapTile(1);
		distController.addPositionToHeatMapTile(2);

		assertThat(distController.getDistributionInPercentOfHeatMapTile(1), is(50.0));
		assertThat(distController.getDistributionInPercentOfHeatMapTile(2), is(50.0));
	}

}
