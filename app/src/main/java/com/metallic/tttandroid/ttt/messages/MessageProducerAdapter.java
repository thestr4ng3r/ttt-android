// TeleTeachingTool - Presentation Recording With Automated Indexing
//
// Copyright (C) 2003-2008 Peter Ziewer - Technische Universit???t M???nchen
// 
//    This file is part of TeleTeachingTool.
//
//    TeleTeachingTool is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    TeleTeachingTool is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with TeleTeachingTool.  If not, see <http://www.gnu.org/licenses/>.

/*
 * Created on 09.01.2006
 *
 * Author: Peter Ziewer, TU Munich, Germany - ziewer@in.tum.de
 */
package com.metallic.tttandroid.ttt.messages;

import java.util.ArrayList;

import com.metallic.tttandroid.ttt.core.ProtocolPreferences;

public abstract class MessageProducerAdapter implements MessageProducer {

	private final ArrayList<MessageConsumer> messageConsumers = new ArrayList<MessageConsumer>();

	// register listener
	@Override
	public void addMessageConsumer(MessageConsumer messageConsumer) {
		messageConsumers.add(messageConsumer);
	}

	// unregister listener
	@Override
	public void removeMessageConsumer(MessageConsumer messageConsumer) {
		messageConsumers.remove(messageConsumer);
	}

	public void deliverMessages(Message[] messages) {
		for (int i = 0; i < messages.length; i++) {
			deliverMessage(messages[i]);
		}
	}

	@Override
	synchronized public void deliverMessage(Message message) {
		int i = 0;
		while (i < messageConsumers.size()) {
			// TODO: maybe clone message
			// TODO: error handling
			try {
				messageConsumers.get(i).handleMessage(message);
			} catch (Exception e) {
				// TODO: maybe: remove consumer
			}
			i++;
		}
	}

	@Override
	abstract public ProtocolPreferences getProtocolPreferences();
}
