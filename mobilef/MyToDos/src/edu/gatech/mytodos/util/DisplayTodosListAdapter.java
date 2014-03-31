package edu.gatech.mytodos.util;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import edu.gatech.mytodos.DisplayTodosActivity;
import edu.gatech.mytodos.R;
import edu.gatech.mytodos.model.Task;

/**
 * Adapter to implement various special effects on {@link DisplayTodosActivity}.
 * It provides: <br>
 * - customized {@link ListView} <br>
 * - coloring of todo based on its priority <br>
 * - highlight of todo when clicked on
 */
public class DisplayTodosListAdapter extends BaseAdapter {

	private List<Task> mListData = null;
	private LayoutInflater mlayoutInflater = null;
	// to keep track which task gets clicked on
	private DisplayTodosListContext postitionContext = null;

	public DisplayTodosListAdapter(Context context, List<Task> listData,
			DisplayTodosListContext positionContext) {
		this.mListData = listData;
		this.mlayoutInflater = LayoutInflater.from(context);
		this.postitionContext = positionContext;
	}

	@Override
	public int getCount() {
		return mListData.size();
	}

	@Override
	public Object getItem(int position) {
		return mListData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		RowViewHolder holder = null;
		if (convertView == null) {
			convertView = this.mlayoutInflater.inflate(
					R.layout.activity_display_todos_list_content_row, null);
			holder = new RowViewHolder();
			holder.setDueDateView((TextView) convertView
					.findViewById(R.id.listContentDueDate));
			holder.setPriorityView((TextView) convertView
					.findViewById(R.id.listContentPriority));
			holder.setSubjectView((TextView) convertView
					.findViewById(R.id.listContentSubject));
			convertView.setTag(holder);
		} else {
			holder = (RowViewHolder) convertView.getTag();
		}

		Task task = mListData.get(position);

		// set row data
		holder.getDueDateView().setText(
				HelperUtil.convertDateToStr(task.getDueDate()));

		String priorityStr = "";
		switch (task.getPriority()) {
		case Task.TASK_PRIORITY_HIGH:
			priorityStr = convertView.getResources().getString(
					R.string.highPriorityLBL);
			break;
		case Task.TASK_PRIORITY_MEDIUM:
			priorityStr = convertView.getResources().getString(
					R.string.medPriorityLBL);
			break;
		case Task.TASK_PRIORITY_LOW:
			priorityStr = convertView.getResources().getString(
					R.string.lowPriorityLBL);
			break;
		default:
			priorityStr = convertView.getResources().getString(
					R.string.medPriorityLBL);
			break;
		}
		holder.getPriorityView().setText(priorityStr);

		holder.getSubjectView().setText(task.getSubject());

		// customize color for individual row
		if (task.isCompleted()) {
			setRowColor(holder, Color.GRAY);
		} else {
			if (task.getPriority() == Task.TASK_PRIORITY_HIGH) {
				setRowColor(holder, Color.RED);
			} else if (task.getPriority() == Task.TASK_PRIORITY_MEDIUM) {
				setRowColor(holder, Color.BLACK);
			} else if (task.getPriority() == Task.TASK_PRIORITY_LOW) {
				setRowColor(holder, Color.GREEN);
			}
		}

		this.postitionContext.addToListItemPositionTaskIDMap(position,
				task.getTaskID());
		this.postitionContext.addToListItemPositionStatusMap(position,
				task.isCompleted());
		return convertView;
	}

	private void setRowColor(RowViewHolder holder, int color) {
		holder.getDueDateView().setTextColor(color);
		holder.getPriorityView().setTextColor(color);
		holder.getSubjectView().setTextColor(color);
	}

	private class RowViewHolder {
		TextView mDueDateView;
		TextView mPriorityView;
		TextView mSubjectView;

		public TextView getDueDateView() {
			return mDueDateView;
		}

		public void setDueDateView(TextView mDueDateView) {
			this.mDueDateView = mDueDateView;
		}

		public TextView getPriorityView() {
			return mPriorityView;
		}

		public void setPriorityView(TextView mPriorityView) {
			this.mPriorityView = mPriorityView;
		}

		public TextView getSubjectView() {
			return mSubjectView;
		}

		public void setSubjectView(TextView mSubjectView) {
			this.mSubjectView = mSubjectView;
		}
	}
}