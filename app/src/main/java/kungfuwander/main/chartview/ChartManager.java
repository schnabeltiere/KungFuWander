package kungfuwander.main.chartview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import kungfuwander.main.chartview.animation.AnimationManager;
import kungfuwander.main.chartview.animation.data.AnimationValue;
import kungfuwander.main.chartview.draw.DrawManager;
import kungfuwander.main.chartview.draw.data.Chart;

public class ChartManager implements AnimationManager.AnimationListener {

	private DrawManager drawManager;
	private AnimationManager animationManager;
	private AnimationListener listener;

	public interface AnimationListener {

		void onAnimationUpdated();
	}


	public ChartManager(@NonNull Context context, @Nullable AnimationListener listener) {
		this.drawManager = new DrawManager(context);
		this.animationManager = new AnimationManager(drawManager.chart(), this);
		this.listener = listener;
	}

	public Chart chart() {
		return drawManager.chart();
	}

	public DrawManager drawer() {
		return drawManager;
	}

	public void animate() {
		if (!drawManager.chart().getDrawData().isEmpty()) {
			animationManager.animate();
		}
	}

	@Override
	public void onAnimationUpdated(@NonNull AnimationValue value) {
		drawManager.updateValue(value);
		if (listener != null) {
			listener.onAnimationUpdated();
		}
	}
}
