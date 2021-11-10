package game;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.function.Consumer;

import engine.AdventureCharacter;
import engine.Command;
import engine.CommandHandler;
import engine.CommandType;
import engine.Direction;
import engine.Enemy;
import engine.Engine;
import engine.EventHandler;
import engine.GameDescription;
import engine.Gateway;
import engine.InvalidCommandException;
import engine.Inventory;
import engine.Item;
import engine.ItemContainer;
import engine.ParserOutput;
import engine.Room;
import engine.Weapon;
import hashedGraph.WeightedHashedGraph;

public class Asylum extends GameDescription implements Serializable {
	private static final long serialVersionUID = -78135229798072209L;
	private Locale lang= Manager.locale;
	private static Object lock = new Object();
	private static Manager frame;
	private String player;
	transient HandleDB db;


	//game params
	Integer health, maxMoves;
	Boolean gasVuln, breathedGas, compassUsed;
	transient WeightedHashedGraph<Room, Gateway> m = new WeightedHashedGraph<Room, Gateway>();
	final EventHandler invalidCommand = new EventHandler() {

		@Override
		public void accept(GameDescription t) {
			// TODO Auto-generated method stub
			System.out.println(ResourceBundle.getBundle("InvalidCommandException", ((Asylum)t).lang).getString("standard"));
		}
	};

	@Override
	public void init() throws Exception {
		// TODO Auto-generated method stub
		frame = new Manager();
		Thread t= new Thread( new Runnable() {
			@Override
			public void run() {
				synchronized(lock) {
	                while (frame.isVisible())
	                    try {
	                        lock.wait();
	                    } catch (InterruptedException e) {
	                        System.out.println(e.getMessage());
	                    }
	            }
	        }

			});
		frame.addWindowListener(new WindowAdapter() {

	        @Override
	        public void windowClosing(WindowEvent arg0) {
	            synchronized (lock) {
	                //frame.setVisible(false);
	                frame.onClose();
	                lock.notify();
	            }
	        }

	    });
		frame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
				super.windowClosed(e);
				synchronized (lock) {
	                frame.setVisible(false);
	                lock.notify();
	            }
			}

		});
		t.start();
		db = new HandleDB();
		t.join();
		player=frame.getPlayer();
		if(player==null) {
			Thread.currentThread().interrupt();
			db.closeConnection();
			return;
		}
		if(frame.getSave()==null) {
			initNew();
			//inserimento in db
	        this.db.insertionTuple(this.player, this);
		}else {
			initFromSave((Asylum) frame.getSave());
			if(!this.lang.equals(Manager.locale)) {
				this.lang=Manager.locale;
				loadLocales();
			}
		}
		if(lang.equals(Locale.ITALIAN) || lang.equals(Locale.ITALY)) {
			Engine.parser = new ParserIT();
		}else {
			Engine.parser = new ParserEN();
		}
		db.closeConnection();
	}

	private void initNew() throws SQLException, CloneNotSupportedException {
		health = 100;
		gasVuln = true;
		breathedGas = false;
		maxMoves = 4;
		compassUsed = false;


		ResourceBundle com = ResourceBundle.getBundle("command", lang);
		Command nord = new Command(CommandType.NORD, com.getString("name_command_nord"));
        nord.setAlias(com.getString("alias_command_nord").split(" "));
        getCommands().add(nord);
        Command inventory = new Command(CommandType.INVENTORY, com.getString("name_command_inventory"));
        inventory.setAlias(com.getString("alias_command_inventory").split(" "));
        getCommands().add(inventory);
        Command sud = new Command(CommandType.SOUTH, com.getString("name_command_south"));
        sud.setAlias(com.getString("alias_command_south").split(" "));
        getCommands().add(sud);
        Command est = new Command(CommandType.EAST, com.getString("name_command_east"));
        est.setAlias(com.getString("alias_command_east").split(" "));
        getCommands().add(est);
        Command ovest = new Command(CommandType.WEST, com.getString("name_command_west"));
        ovest.setAlias(com.getString("alias_command_west").split(" "));
        getCommands().add(ovest);
        Command nord_est = new Command(CommandType.NORTH_EAST, com.getString("name_command_north_east"));
        nord_est.setAlias(com.getString("alias_command_north_east").split(" "));
        getCommands().add(nord_est);
        Command nord_ovest = new Command(CommandType.NORTH_WEST, com.getString("name_command_north_west"));
        nord_ovest.setAlias(com.getString("alias_command_north_west").split(" "));
        getCommands().add(nord_ovest);
        Command sud_est = new Command(CommandType.SOUTH_EAST, com.getString("name_command_south_east"));
        sud_est.setAlias(com.getString("alias_command_south_east").split(" "));
        getCommands().add(sud_est);
        Command sud_ovest = new Command(CommandType.SOUTH_WEST, com.getString("name_command_south_west"));
        sud_ovest.setAlias(com.getString("alias_command_south_west").split(" "));
        getCommands().add(sud_ovest);
        Command end = new Command(CommandType.END, com.getString("name_command_end"));
        end.setAlias(com.getString("alias_command_end").split(" "));
        getCommands().add(end);
        Command look = new Command(CommandType.LOOK_AT, com.getString("name_command_look_at"));
        look.setAlias(com.getString("alias_command_look_at").split(" "));
        getCommands().add(look);
        Command pickup = new Command(CommandType.PICK_UP, com.getString("name_command_pick_up"));
        pickup.setAlias(com.getString("alias_command_pick_up").split(" "));
        getCommands().add(pickup);
        Command open = new Command(CommandType.OPEN, com.getString("name_command_open"));
        open.setAlias(new String[]{});
        getCommands().add(open);
        Command close = new Command(CommandType.CLOSE, com.getString("name_command_close"));
        close.setAlias(new String[]{});
        getCommands().add(close);
        Command _break = new Command(CommandType.BREAK, com.getString("name_command_break"));
        _break.setAlias(com.getString("alias_command_break").split(" "));
        getCommands().add(_break);
        Command talk_to = new Command(CommandType.TALK_TO, com.getString("name_command_talk_to"));
        talk_to.setAlias(com.getString("alias_command_talk_to").split(" "));
        getCommands().add(talk_to);
        Command walk_to = new Command(CommandType.WALK_TO, com.getString("name_command_walk_to"));
        walk_to.setAlias(com.getString("alias_command_walk_to").split(" "));
        getCommands().add(walk_to);
        Command up = new Command(CommandType.UP, com.getString("name_command_up"));
        up.setAlias(com.getString("alias_command_up").split(" "));
        getCommands().add(up);
        Command down = new Command(CommandType.DOWN, com.getString("name_command_down"));
        down.setAlias(com.getString("alias_command_down").split(" "));
        getCommands().add(down);
        Command use = new Command(CommandType.USE, com.getString("name_command_use"));
        use.setAlias(com.getString("alias_command_use").split(" "));
        getCommands().add(use);
        Command turn_on = new Command(CommandType.TURN_ON, com.getString("name_command_turn_on"));
        turn_on.setAlias(new String[]{""});
        getCommands().add(turn_on);
        Command turn_off = new Command(CommandType.TURN_OFF, com.getString("name_command_turn_off"));
        turn_off.setAlias(new String[]{""});
        getCommands().add(turn_off);
        Command drop = new Command(CommandType.DROP, com.getString("name_command_drop"));
        drop.setAlias(com.getString("alias_command_drop").split(" "));
        getCommands().add(drop);

        //set inv
        setInventory(new Inventory());

      //Rooms
        ResourceBundle r = ResourceBundle.getBundle("room", lang);
        Room room1 = new Room(r.getString("descr_room_1"),r.getString("look_room_1"),r.getString("name_room_1"));
        m.insNode(room1);
        Room room2 = new Room(r.getString("descr_room_2"),r.getString("look_room_2"),r.getString("name_room_2"));
        m.insNode(room2);
        Room room3 = new Room(r.getString("descr_room_3"),r.getString("look_room_3"),r.getString("name_room_3"));
        m.insNode(room3);
        Room room4 = new Room(r.getString("descr_room_4"),r.getString("look_room_4"),r.getString("name_room_4"));
        m.insNode(room4);
        Room room5 = new Room(r.getString("descr_room_5"), r.getString("look_room_5"), r.getString("name_room_5"));
        m.insNode(room5);
        Room room6 = new Room(r.getString("descr_room_6"),r.getString("look_room_6"),r.getString("name_room_6"));
        m.insNode(room6);
        Room room7 = new Room(r.getString("descr_room_7"), r.getString("look_room_7"), r.getString("name_room_7"));
        m.insNode(room7);
        Room room8 = new Room(r.getString("descr_room_8"), r.getString("look_room_8"), r.getString("name_room_8"));
        m.insNode(room8);
        Room hallway = new Room(r.getString("descr_room_9"), r.getString("look_room_9"), r.getString("name_room_9"));
        m.insNode(hallway);

        Room hallway2 = new Room(r.getString("descr_room_10"), r.getString("look_room_10"), r.getString("name_room_10"));
        m.insNode(hallway2);
        Room hallway3 = new Room(r.getString("descr_room_11"), r.getString("look_room_11"), r.getString("name_room_11"));
        m.insNode(hallway3);
        Room bathroom = new Room("", r.getString("look_room_12"), r.getString("name_room_12"));
        m.insNode(bathroom);


      //second floor
        Room hallway4 = new Room(r.getString("descr_room_13"), r.getString("look_room_13"), r.getString("name_room_13"));
  		m.insNode(hallway4);
  		Room infirmary = new Room(r.getString("descr_room_14"), r.getString("look_room_14"), r.getString("name_room_14"));
  		m.insNode(infirmary);
  		Room surgery = new Room("", r.getString("look_room_15"), r.getString("name_room_15"));
  		m.insNode(surgery);
  		Room surveillance = new Room(r.getString("descr_room_16"), r.getString("look_room_16"), r.getString("name_room_16"));
  		m.insNode(surveillance);
  		Room paddedCell = new Room(r.getString("descr_room_17"), r.getString("look_room_17"), r.getString("name_room_17"));
  		paddedCell.setLight(false);

  		Room office = new Room(r.getString("descr_room_18"), r.getString("look_room_18"), r.getString("name_room_18"));
  		m.insNode(office);

  		Room exit = new Room(r.getString("descr_room_19"), r.getString("look_room_19"), r.getString("name_room_19"));


  		//item
        ResourceBundle i = ResourceBundle.getBundle("item", lang);
        Item bed = new Item(i.getString("name_item_1"), i.getString("descr_item_1"), null);
		bed.setHandler(new CommandHandler() {

			@Override
			public EventHandler apply(CommandType t) {
				// TODO Auto-generated method stub
				switch (t) {
				case LOOK_AT:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							System.out.println(bed.getDescription());
						}
					};
				default:
					return invalidCommand;
				}
			}
		});

		final Weapon screwdriver = new Weapon(i.getString("name_item_2"), i.getString("descr_item_2"), null, 10, 5, 15);
		screwdriver.setHandler(new CommandHandler() {
			@Override
			public EventHandler apply(CommandType t) {
				// TODO Auto-generated method stub
				switch (t) {
				case LOOK_AT:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							System.out.println(screwdriver.getDescription());
						}
					};
				case USE:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							if(t.getCurrentEnemy()!=null && t.getCurrentRoom().hasLight()) {
								if(screwdriver.getShots() != 0) {
									t.getCurrentEnemy().setHealth(t.getCurrentEnemy().getHealth()-screwdriver.getDamage());
									screwdriver.setShots(screwdriver.getShots() - 1);
									switch(screwdriver.getShots()) {
									case 0:
										System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("case_0_item_2"));
										break;
									case 1:
										System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("case_1_item_2"));
										break;
									case 4:
										System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("case_4_item_2"));
										break;
									case 7:
										System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("case_7_item_2"));
										break;
									}
								} else System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("unusable_item_2"));
							} else System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("no_target"));
						}
					};
				case DROP:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							EventHandler.drop(screwdriver, t);
							System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("drop_item_2"));
						}
					};
				case PICK_UP:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							try {
								EventHandler.pickUp(screwdriver, t);
								System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("pick_item_2"));
							} catch (InvalidCommandException e) {
								// TODO Auto-generated catch block
								System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("no_item_room"));
							}
						}
					};
				default:
					return invalidCommand;
				}
			}
		});

		final Item gasmask = new Item(i.getString("name_item_3"), i.getString("descr_item_3"), null);
		gasmask.setHandler(new CommandHandler() {
			@Override
			public EventHandler apply(CommandType t) {
				switch(t) {
				case LOOK_AT:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							System.out.println(gasmask.getDescription());
						}
					};
				case USE:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("use_item_3"));
							((Asylum) t).gasVuln = false;
							bathroom.setDescription(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("descr_bathroom_mask"));
					     	surgery.setDescription(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("descr_surgery_mask"));
					     	if(!compassUsed) {
					     		bathroom.setLook(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("look_bathroom_mask_compass"));
							 	surgery.setLook(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("look_surgery_mask_compass"));

					     	}
					     	else {
					     		bathroom.setLook(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("look_bathroom_mask"));
							 	surgery.setLook(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("look_surgery_mask"));
						}
						}
					};
				case PICK_UP:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							try {
								EventHandler.pickUp(gasmask, t);
								System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("pick_item_3"));
							} catch (InvalidCommandException e) {
								// TODO Auto-generated catch block
								System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("no_item_room"));
							}
						}
					};
				case DROP:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							EventHandler.drop(gasmask, t);
							((Asylum) t).gasVuln = true;
							System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("drop_item_3"));
							bathroom.setDescription(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("descr_bathroom_drop_mask"));
					        surgery.setDescription(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("descr_surgery_drop_mask"));
							if(compassUsed) {
								bathroom.setLook(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("look_bathroom_drop_mask_compass"));
						        surgery.setLook(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("look_surgery_drop_mask_compass"));
						        }
							else {
								bathroom.setLook(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("look_bathroom_drop_mask"));
						        surgery.setLook(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("look_surgery_drop_mask"));
						}
						}
					};
				default:
					return invalidCommand;
				}
			}
		});

		final Item torch = new Item(i.getString("name_item_4"), i.getString("descr_item_4"), null);
		torch.setHandler(new CommandHandler() {
			@Override
			public EventHandler apply(CommandType t) {
				// TODO Auto-generated method stub
				switch (t) {
				case USE:
				case TURN_ON:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("turn_on_item_4"));
							t.getCurrentRoom().setLight(true);
							if (t.getCurrentRoom().equals(paddedCell) && t.getCurrentRoom().getTrap()!= null) {
								t.getCurrentRoom().getTrap().accept(t);
							}
						}
					};
				case TURN_OFF:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							t.getCurrentRoom().setVisible(t.getCurrentRoom().hasLight());
							System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("turn_off_item_4"));
						}
					};
				case LOOK_AT:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							System.out.println(torch.getDescription());
						}
					};
				case DROP:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							EventHandler.drop(torch, t);
							System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("drop_item_4"));
						}
					};
				case PICK_UP:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							try {
								EventHandler.pickUp(torch, t);
								System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("pick_item_4"));
							} catch (InvalidCommandException e) {
								// TODO Auto-generated catch block
								System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("no_item_room"));
							}
						}
					};
				default:
					return invalidCommand;
				}
			}
		});

		final Item pills = new Item(i.getString("name_item_5"), i.getString("descr_item_5"), null);
		pills.setHandler(new CommandHandler() {
			@Override
			public EventHandler apply(CommandType t) {
				// TODO Auto-generated method stub
				switch (t) {
				case USE:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							if(t.getInventory().getList().contains(pills)) {
								System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("use_item_5"));
								((Asylum) t).breathedGas = false;
								t.getInventory().remove(pills);
							} else System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("no_item_inventory"));
						}
					};
				case LOOK_AT:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							System.out.println(pills.getDescription());
						}
					};
				case DROP:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							EventHandler.drop(pills, t);
							System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("drop_item_5"));
						}
					};
				case PICK_UP:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							try {
								EventHandler.pickUp(pills, t);
								System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("pick_item_5"));
							} catch (InvalidCommandException e) {
								// TODO Auto-generated catch block
								System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("no_item_room"));
							}
						}
					};
				default:
					return invalidCommand;
				}
			}
		});

		final Item adrenaline = new Item(i.getString("name_item_6"), i.getString("descr_item_6"), null);
		adrenaline.setHandler(new CommandHandler() {
			@Override
			public EventHandler apply(CommandType t) {
				// TODO Auto-generated method stub
				switch (t) {
				case USE:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							if(t.getInventory().getList().contains(adrenaline)) {
								System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("use_item_6"));
								((Asylum) t).health += 20;
								System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("show_health"));
								t.getInventory().remove(adrenaline);
							} else System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("no_item_inventory"));
						}
					};
				case LOOK_AT:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							System.out.println(adrenaline.getDescription());
						}
					};
				case DROP:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							EventHandler.drop(adrenaline, t);
							System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("drop_item_6"));
						}
					};
				case PICK_UP:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							try {
								EventHandler.pickUp(adrenaline, t);
								System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("pick_item_6"));
							} catch (InvalidCommandException e) {
								// TODO Auto-generated catch block
								System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("no_item_room"));
							}
						}
					};
				default:
					return invalidCommand;
				}
			}
		});

		final Item mirrorBathroom = new Item(i.getString("name_item_7"), i.getString("descr_item_7"), null);
		mirrorBathroom.setHandler(new CommandHandler() {
			@Override
			public EventHandler apply(CommandType t) {
				// TODO Auto-generated method stub
				switch (t) {
				case LOOK_AT:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							System.out.println(mirrorBathroom.getDescription());
						}
					};
				case BREAK:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							if(!mirrorBathroom.isPushed()) {
								t.getCurrentRoom().getObjects().add(pills);
								mirrorBathroom.setPushed(true);
								System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("break_item_7"));
							}
						}
					};
				default:
					return invalidCommand;
				}
			}
		});

		final Item compass = new Item(i.getString("name_item_8"), i.getString("descr_item_8"), null);
		compass.setHandler(new CommandHandler() {
			@Override
			public EventHandler apply(CommandType t) {
				// TODO Auto-generated method stub
				switch (t) {
				case LOOK_AT:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							System.out.println(compass.getDescription());
						}
					};
				case USE:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("use_item_8"));
							room1.setLook(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("look_room_1_compass"));
							room2.setLook(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("look_room_2_compass"));
							room3.setLook(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("look_room_3_compass"));
							room4.setLook(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("look_room_4_compass"));
							room5.setLook(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("look_room_5_compass"));
							room6.setLook(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("look_room_6_compass"));
							room7.setLook(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("look_room_7_compass"));
							room8.setLook(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("look_room_8_compass"));
							hallway.setLook(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("look_room_9_compass"));
							hallway2.setLook(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("look_room_10_compass"));
							hallway3.setLook(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("look_room_11_compass"));
							if (((Asylum) t).gasVuln == true) {
								bathroom.setLook(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("look_room_12_compass_no_mask"));
									}
							else {
								bathroom.setLook(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("look_room_12_compass"));
									}
							hallway4.setLook(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("look_room_13_compass"));
							infirmary.setLook(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("look_room_14_compass"));
							if (((Asylum) t).gasVuln == true) {
								surgery.setLook(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("look_room_15_compass_no_mask"));
									}
							else {
								surgery.setLook(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("look_room_15_compass"));
									}
							surveillance.setLook(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("look_room_16_compass"));
							paddedCell.setLook(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("look_room_17_compass"));
							office.setLook(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("look_room_18_compass"));
							((Asylum) t).compassUsed = true;

						}
					};
				case DROP:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							EventHandler.drop(compass, t);
							System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("drop_item_8"));
							((Asylum) t).compassUsed = false;
						}
					};
				case PICK_UP:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							try {
								EventHandler.pickUp(compass, t);
								System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("pick_item_8"));
							} catch (InvalidCommandException e) {
								// TODO Auto-generated catch block
								System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("no_item_room"));
							}
						}
					};
				default:
					return invalidCommand;
				}
			}
		});

		final ItemContainer chest = new ItemContainer(i.getString("name_item_9"), i.getString("descr_item_9"), null);
		chest.setHandler(new CommandHandler() {
			@Override
			public EventHandler apply(CommandType t) {
				// TODO Auto-generated method stub
				switch (t) {
				case LOOK_AT:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							if(!chest.isOpened()) {
								System.out.println(chest.getDescription());
							}else {
								System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("look_item_9"));
								for(Item i: chest.getContent()) {
									System.out.println(i.getName());
								}
								for(Item i: chest.getContent()) {
									if(!t.getCurrentRoom().getObjects().contains(i)) t.getCurrentRoom().getObjects().add(i);
								}
							}
						}
					};
				case OPEN:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							if(chest.isOpened()) {
								System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("opened_chest"));
							}else if(!chest.isOpened() && !chest.isLocked()) {
									System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("open_chest"));
									chest.setOpened(true);
								  }else if(chest.isLocked()) {
									System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("locked_chest"));
								  }
						}
					};
				case CLOSE:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
								if (!chest.isOpened()) {
									System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("closed_chest"));}
									else {
										System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("close_chest"));
										for(Item i: chest.getContent()) t.getCurrentRoom().getObjects().remove(i);
										chest.setOpened(false);
									}
								}

					};
				default:
					return invalidCommand;
				}
			}
		});

		final Item pc = new Item(i.getString("name_item_10"), i.getString("descr_item_10"), null);
		pc.setHandler(new CommandHandler() {
			@Override
			public EventHandler apply(CommandType t) {
				// TODO Auto-generated method stub
				switch (t) {
				case USE:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("look_item_10"));						}
					};
				case LOOK_AT:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							System.out.println(pc.getDescription());
						}
					};
				default:
					return invalidCommand;
				}
			}
		});

		final Weapon scalpel = new Weapon(i.getString("name_item_11"), i.getString("descr_item_11"), null, 5, 10, 30);
		scalpel.setHandler(new CommandHandler() {
			@Override
			public EventHandler apply(CommandType t) {
				// TODO Auto-generated method stub
				switch (t) {
				case LOOK_AT:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							System.out.println(scalpel.getDescription());
						}
					};
				case USE:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							if(t.getCurrentEnemy()!=null && t.getCurrentRoom().hasLight()) {
								if(scalpel.getShots() != 0) {
									t.getCurrentEnemy().setHealth(t.getCurrentEnemy().getHealth()-scalpel.getDamage());
									scalpel.setShots(scalpel.getShots() - 1);
									switch(scalpel.getShots()) {
									case 0:
										System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("case_0_item_11"));
										break;
									case 1:
										System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("case_1_item_11"));
										break;
									case 4:
										System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("case_4_item_11"));
										break;
									}
								} else System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("unusable_item_11"));
							}else System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("no_target"));
						}
					};
				case DROP:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							EventHandler.drop(scalpel, t);
							System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("drop_item_11"));
						}
					};
				case PICK_UP:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							try {
								EventHandler.pickUp(scalpel, t);
								System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("pick_item_11"));
							} catch (InvalidCommandException e) {
								// TODO Auto-generated catch block
								System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("no_item_room"));
							}
						}
					};
				default:
					return invalidCommand;
				}
			}
		});

		final Weapon gun = new Weapon(i.getString("name_item_12"), i.getString("descr_item_12"), null, 7, 20, 40);
		gun.setHandler(new CommandHandler() {
			@Override
			public EventHandler apply(CommandType t) {
				// TODO Auto-generated method stub
				switch (t) {
				case LOOK_AT:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							System.out.println(gun.getDescription());
						}
					};
				case USE:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							if(t.getCurrentEnemy()!=null && t.getCurrentRoom().hasLight()) {
								if(gun.getShots() != 0) {
									t.getCurrentEnemy().setHealth(t.getCurrentEnemy().getHealth()-gun.getDamage());
									gun.setShots(gun.getShots() - 1);
									switch(gun.getShots()) {
									case 0:
										System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("case_0_item_12"));
										break;
									case 1:
										System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("case_1_item_12"));
										break;
									case 5:
										System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("case_5_item_12"));
										break;
									}
								} else System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("unusable_item_12"));
							}else System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("no_target"));
						}
					};
				case DROP:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							EventHandler.drop(gun, t);
							System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("drop_item_12"));
						}
					};
				case PICK_UP:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							try {
								EventHandler.pickUp(gun, t);
								System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("pick_item_12"));
							} catch (InvalidCommandException e) {
								// TODO Auto-generated catch block
								System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("no_item_room"));
							}
						}
					};
				default:
					return invalidCommand;
				}
			}
		});

		final Item mirrorCell = new Item(i.getString("name_item_13"), i.getString("descr_item_13"), null);
		mirrorCell.setHandler(new CommandHandler() {
			@Override
			public EventHandler apply(CommandType t) {
				// TODO Auto-generated method stub
				switch (t) {
				case LOOK_AT:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							System.out.println(mirrorCell.getDescription());
						}
					};
				case BREAK:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							WeightedHashedGraph<Room, Gateway> m = t.getMap();
							if(!mirrorCell.isPushed()) {
								//inserire l'arco
								m.insArc(paddedCell, office, new Gateway(Direction.SOUTH));
								mirrorCell.setPushed(true);
								System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("break_item_13"));
							}
						}
					};
				default:
					return invalidCommand;
				}
			}
		});

		final Item codePaper = new Item(i.getString("name_item_14"), i.getString("descr_item_14"), null);
		codePaper.setHandler(new CommandHandler() {

			@Override
			public EventHandler apply(CommandType t) {
				switch(t) {
				case LOOK_AT:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							System.out.println(codePaper.getDescription());
						}
					};
				case PICK_UP:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							try {
								EventHandler.pickUp(codePaper, t);
								System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("pick_item_14"));
							} catch (InvalidCommandException e) {
								// TODO Auto-generated catch block
								System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("no_item_room"));
							}
						}
					};
				case DROP:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							EventHandler.drop(codePaper, t);
							System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("drop_item_14"));
						}
					};
				default:
					return invalidCommand;
				}};
		});

		final Item blockNotes = new Item(i.getString("name_item_15"),  i.getString("descr_item_15"), null);
		blockNotes.setHandler(new CommandHandler() {

			@Override
			public EventHandler apply(CommandType t) {
				switch(t) {
				case LOOK_AT:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							System.out.println(blockNotes.getDescription());
						}
					};
				case PICK_UP:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							try {
								EventHandler.pickUp(blockNotes, t);
								System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("pick_item_15"));
							} catch (InvalidCommandException e) {
								// TODO Auto-generated catch block
								System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("no_item_room"));
							}
						}
					};
				case DROP:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							EventHandler.drop(blockNotes, t);
							System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("drop_item_15"));
						}
					};
				default:
					return invalidCommand;
				}};
		});

		final Item keypad = new Item(i.getString("name_item_16"), i.getString("descr_item_16"), null);
		keypad.setHandler(new CommandHandler() {

			@Override
			public EventHandler apply(CommandType t) {
				switch(t) {
				case LOOK_AT:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							System.out.println(keypad.getDescription());
						}
					};
				case USE:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							if (hallway3.getTrap()!=null){
								if(!keypad.isPushed()) {
									Scanner scan = new Scanner(System.in);
									System.out.println(":");
									String codEntered = scan.nextLine();
									String[] tokens = codePaper.getDescription().split("\\s+");
									if(codEntered.equals(tokens[3])) {
										hallway3.setTrap(null);
										System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("accepted_item_16"));
										keypad.setPushed(true);
									} else System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("error_item_16"));
								} else System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("trap_off"));
							}else System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("trap_off"));

						}
					};
				default:
					return invalidCommand;
				}};
		});


		Inventory corpseInv = new Inventory();

        ResourceBundle e = ResourceBundle.getBundle("enemy", lang);
		final AdventureCharacter corpse = new AdventureCharacter(0, e.getString("name_enemy_1"), e.getString("descr_enemy_1"), null, corpseInv, null);
		final Enemy mutant = new Enemy(55, e.getString("name_enemy_2"), e.getString("descr_enemy_2"), e.getString("talk_to_enemy_2"), null, codePaper,5,10);
		mutant.setInv(new Inventory());

		final Item key = new Item(i.getString("name_item_17"), i.getString("descr_item_17"), null);
		key.setHandler(new CommandHandler() {

			@Override
			public EventHandler apply(CommandType t) {
				switch(t) {
				case LOOK_AT:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							System.out.println(key.getDescription());
						}
					};
				case USE:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							WeightedHashedGraph<Room, Gateway> m = t.getMap();
							if(t.getInventory().getList().contains(key)) {
								if(t.getCurrentRoom().equals(room1)) {
									try {
										for(Room a : m.getAdjacents(t.getCurrentRoom())) {
											if(m.readArc(t.getCurrentRoom(), a).getLockedBy()==key.getId()) {
												if (!m.readArc(t.getCurrentRoom(), a).isLocked()) System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("opened_door"));
												else {
													m.readArc(t.getCurrentRoom(), a).setLocked(false);
													System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("use_item_17"));
													EventHandler.drop(key, t);
													break;
												}
											}
										}
									} catch (Exception e) {
										System.out.println(e.getMessage());
									}
								}else System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("no_room_key"));
							} else System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("pick_key_to_use"));
						}
					};
				case PICK_UP:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							try {
								EventHandler.pickUp(key, t);
								corpse.setDescription(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("descr_corpse_key"));
								System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("pick_item_17"));
							} catch (InvalidCommandException e) {
								// TODO Auto-generated catch block
								System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("no_item_room"));
							}

						}
					};
				case DROP:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							EventHandler.drop(key, t);
							System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("drop_item_17"));
						}
					};
				default:
					return invalidCommand;
				}};
		});

		corpseInv.add(key);


		Item key_1= new Item(i.getString("name_item_18"), i.getString("descr_item_18"), null);
		key_1.setHandler(new CommandHandler() {

			@Override
			public EventHandler apply(CommandType t) {
				switch(t) {
				case LOOK_AT:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							System.out.println(key_1.getDescription());
						}
					};
				case USE:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							WeightedHashedGraph<Room, Gateway> m = t.getMap();
							if(t.getInventory().getList().contains(key_1)) {
								if(t.getCurrentRoom().equals(paddedCell)) {
									try {
										for(Room a : m.getAdjacents(t.getCurrentRoom())) {
											if(m.readArc(t.getCurrentRoom(), a).getLockedBy()==key_1.getId()) {
												if (!m.readArc(t.getCurrentRoom(), a).isLocked()) System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("opened_door"));
												else {
													m.readArc(t.getCurrentRoom(), a).setLocked(false);
													System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("use_item_18"));
													EventHandler.drop(key_1, t);
													break;
												}
											}
										}
									} catch (Exception e) {
										System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("no_item_room"));
									}
								}else System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("no_room_key"));
							} else System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("pick_key_to_use"));
						}
					};
				case PICK_UP:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							try {
								EventHandler.pickUp(key_1, t);
								System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("pick_item_18"));
							} catch (InvalidCommandException e) {
								// TODO Auto-generated catch block
								System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("no_item_room"));
							}
						}
					};
				case DROP:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							EventHandler.drop(key_1, t);
							System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("drop_item_18"));
						}
					};
				default:
					return invalidCommand;
				}};
		});

		Item key_2= new Item(i.getString("name_item_19"), i.getString("descr_item_19"), null);
		key_2.setHandler(new CommandHandler() {

			@Override
			public EventHandler apply(CommandType t) {
				switch(t) {
				case LOOK_AT:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							System.out.println(key_2.getDescription());
						}
					};
				case USE:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							WeightedHashedGraph<Room, Gateway> m = t.getMap();
							if(t.getInventory().getList().contains(key_2)) {
								if(t.getCurrentRoom().equals(office)) {
									try {
										for(Room a : m.getAdjacents(t.getCurrentRoom())) {
											if(m.readArc(t.getCurrentRoom(), a).getLockedBy()==key_2.getId()) {
												if (!m.readArc(t.getCurrentRoom(), a).isLocked()) System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("opened_door"));
												else {
													m.readArc(t.getCurrentRoom(), a).setLocked(false);
													System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("use_item_19"));
													EventHandler.drop(key_2, t);
													break;
												}
											}
										}
									} catch (Exception e) {
										System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("no_item_room"));
									}
								}else System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("no_room_key"));
							} else System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("pick_key_to_use"));
						}
					};
				case PICK_UP:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							try {
								EventHandler.pickUp(key_2, t);
								System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("pick_item_19"));
							} catch (InvalidCommandException e) {
								// TODO Auto-generated catch block
								System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("no_item_room"));
							}
						}
					};
				case DROP:
					return new EventHandler() {
						@Override
						public void accept(GameDescription t) {
							// TODO Auto-generated method stub
							EventHandler.drop(key_2, t);
							System.out.println(ResourceBundle.getBundle("item", ((Asylum)t).lang).getString("drop_item_19"));
						}
					};
				default:
					return invalidCommand;
				}};
		});


		final Enemy assistant = new Enemy(100, e.getString("name_enemy_3"), e.getString("descr_enemy_3"),
				e.getString("talk_to_enemy_3"),
				new Inventory(),key_1,5,10);
		final Enemy director = new Enemy(100, e.getString("name_enemy_4"), e.getString("descr_enemy_4"),
				e.getString("talk_to_enemy_4"),
				new Inventory(), key_2,5,20);


		assistant.getInv().add(key_1);
		director.getInv().add(key_2);;
		chest.add(compass);
		chest.add(torch);
		chest.add(blockNotes);

		room1.getEnemies().add(corpse);
		room1.getObjects().add(bed);
		room2.getObjects().add(bed);
		room3.getObjects().add(chest);
		hallway2.getEnemies().add(mutant);
		hallway2.getObjects().add(keypad);
		room4.getObjects().add(bed);
		room5.getObjects().add(bed);
		room6.getObjects().add(bed);
		room6.getObjects().add(screwdriver);
		room7.getObjects().add(bed);
		room8.getObjects().add(gasmask);
		room8.getObjects().add(bed);
		bathroom.getObjects().add(mirrorBathroom);



		infirmary.getObjects().add(pills);
		infirmary.getObjects().add(adrenaline);
		infirmary.getObjects().add(adrenaline);
		surgery.getObjects().add(scalpel);
		surgery.getObjects().add(bed);
		surveillance.getObjects().add(gun);
		surveillance.getObjects().add(pc);
		paddedCell.getObjects().add(mirrorCell);
		paddedCell.getEnemies().add(assistant);
		office.getEnemies().add(director);


		//doors
		m.insArc(room1, hallway, new Gateway(Direction.SOUTH, key.getId(), true));
		m.insArc(hallway, room1, new Gateway(Direction.NORTH_WEST));
		m.insArc(room2, hallway, new Gateway(Direction.SOUTH));
		m.insArc(hallway, room2, new Gateway(Direction.NORTH_EAST));
		m.insArc(room5, hallway, new Gateway(Direction.NORTH));
		m.insArc(hallway, room5, new Gateway(Direction.SOUTH_WEST));
		m.insArc(room6, hallway, new Gateway(Direction.NORTH));
		m.insArc(hallway, room6, new Gateway(Direction.SOUTH_EAST));

		m.insArc(hallway, hallway2, new Gateway(Direction.EAST));
		m.insArc(hallway2, hallway, new Gateway(Direction.WEST));


		m.insArc(room3, hallway2, new Gateway(Direction.SOUTH));
		m.insArc(hallway2, room3, new Gateway(Direction.NORTH_WEST));
		m.insArc(room4, hallway2, new Gateway(Direction.SOUTH));
		m.insArc(hallway2, room4, new Gateway(Direction.NORTH_EAST));
		m.insArc(room7, hallway2, new Gateway(Direction.NORTH));
		m.insArc(hallway2, room7, new Gateway(Direction.SOUTH_WEST));
		m.insArc(room8, hallway2, new Gateway(Direction.NORTH));
		m.insArc(hallway2, room8, new Gateway(Direction.SOUTH_EAST));

		m.insArc(hallway3, hallway2, new Gateway(Direction.WEST));
		m.insArc(hallway2, hallway3, new Gateway(Direction.EAST));
		m.insArc(bathroom, hallway3, new Gateway(Direction.SOUTH));
		m.insArc(hallway3, bathroom, new Gateway(Direction.NORTH));

		m.insArc(hallway3, hallway4, new Gateway(Direction.DOWN));
		m.insArc(hallway4, hallway3, new Gateway(Direction.UP));

		m.insArc(hallway4, infirmary, new Gateway(Direction.NORTH_EAST));
		m.insArc(infirmary, hallway4, new Gateway(Direction.WEST));
		m.insArc(hallway4, surgery, new Gateway(Direction.SOUTH_EAST));
		m.insArc(surgery, hallway4, new Gateway(Direction.WEST));
		m.insArc(hallway4, surveillance, new Gateway(Direction.WEST));
		m.insArc(surveillance, hallway4, new Gateway(Direction.EAST));

		m.insArc(surveillance, paddedCell, new Gateway(Direction.SOUTH));
		m.insArc(paddedCell, surveillance, new Gateway(Direction.NORTH));
		m.insArc(office, exit, new Gateway(Direction.SOUTH, key_2.getId(), true));
		m.insArc(exit, office, new Gateway(Direction.NORTH));
		m.insArc(office, paddedCell, new Gateway(Direction.NORTH));


		//traps

        ResourceBundle tr = ResourceBundle.getBundle("trap", lang);


		hallway2.setTrap(new EventHandler() {

			@Override
			public void accept(GameDescription t) {
				// TODO Auto-generated method stub
				if(getCurrentEnemy()!=null) {
					System.out.println(ResourceBundle.getBundle("trap", ((Asylum)t).lang).getString("descr_room_10"));
				}
			}
		});
		EventHandler gasTrap = new EventHandler() {

			@Override
			public void accept(GameDescription t) {
				// TODO Auto-generated method stub
				Asylum g = (Asylum) t;
				if(g.gasVuln && !g.breathedGas) {
					g.breathedGas = true;
					g.maxMoves = 4;
					System.out.println(ResourceBundle.getBundle("trap", ((Asylum)t).lang).getString("gas_breathing"));
				}
			}
		};

		bathroom.setTrap(gasTrap);
		surgery.setTrap(gasTrap);

		hallway3.setTrap(new EventHandler() {
			@Override
			public void accept(GameDescription t) {
				// TODO Auto-generated method stub
				Asylum g = (Asylum) t;
				g.health = 0;
				System.out.println(ResourceBundle.getBundle("trap", ((Asylum)t).lang).getString("trap_death"));
				Thread.currentThread().interrupt();
			}
		});

		//checkpoint
		hallway4.setTrap(new EventHandler() {

			@Override
			public void accept(GameDescription t) {
				// TODO Auto-generated method stub
				checkpoint((Asylum)t);
				System.out.println(ResourceBundle.getBundle("function", ((Asylum)t).lang));
			}
		});

		//stanza iniziale
		setCurrentRoom(room1);
		System.out.println(tr.getString("intro"));

		paddedCell.setTrap(new EventHandler() {

			@Override
			public void accept(GameDescription t) {
				// TODO Auto-generated method stub
				if (t.getCurrentRoom().hasLight() && getCurrentEnemy()!=null) {
					try {
						t.getMap().readArc(paddedCell, surveillance).setLockedBy(key_1.getId());
						t.getMap().readArc(paddedCell, surveillance).setLocked(true);
						paddedCell.setDescription(ResourceBundle.getBundle("trap", ((Asylum)t).lang).getString("descr_room_17_light"));
						paddedCell.setLook(ResourceBundle.getBundle("trap", ((Asylum)t).lang).getString("look_room_17_light"));
						System.out.println(ResourceBundle.getBundle("trap", ((Asylum)t).lang).getString("trapped"));
					} catch (Exception e) {}
				}
				if (t.getCurrentRoom().hasLight() && getCurrentEnemy()==null) {
					try {
					paddedCell.setDescription(ResourceBundle.getBundle("trap", ((Asylum)t).lang).getString("descr_room_17_no_enemy"));
				}
			 catch (Exception e) {}
		}
			}
		});

		office.setTrap(new EventHandler() {

			@Override
			public void accept(GameDescription t) {
				// TODO Auto-generated method stub
				if (getCurrentEnemy()!=null) {
					try {
						System.out.println(ResourceBundle.getBundle("trap", ((Asylum)t).lang).getString("office_enemy"));

				}
			 catch (Exception e) {}
		}
				if (getCurrentEnemy()==null) {

					office.setLook(ResourceBundle.getBundle("trap", ((Asylum)t).lang).getString("look_room_17_no_enemy"));
					}

			}
		});

		//set mappa
        setMap(m);
	}

	private void initFromSave(Asylum save) throws SQLException, Exception {
		this.breathedGas = save.breathedGas;
		this.gasVuln = save.gasVuln;
		this.health = save.health;
		this.maxMoves = save.maxMoves;
		this.player = save.player;
		this.compassUsed = save.compassUsed;
		this.setMap(save.getMap());
		this.setInventory(save.getInventory());
		this.setCurrentRoom(save.getCurrentRoom());
		this.setCurrentEnemy(save.getCurrentEnemy());
		this.setCommandTarget(save.getCommandTarget());
		this.setCommands(save.getCommands());
		this.lang = save.lang;
	}

	private Room searchDirection(Direction d) throws Exception {
		Collection<Room> ad = getMap().getAdjacents(getCurrentRoom());
		for(Room r: ad) {
			if(getMap().readArc(getCurrentRoom(), r).getDirection()==d)
				return r;
		}
		return null;
	}

	private Direction commandToDirection(CommandType c) {
		switch (c) {
		case NORD:
			return Direction.NORTH;
		case NORTH_EAST:
			return Direction.NORTH_EAST;
		case NORTH_WEST:
			return Direction.NORTH_WEST;
		case SOUTH:
			return Direction.SOUTH;
		case SOUTH_EAST:
			return Direction.SOUTH_EAST;
		case SOUTH_WEST:
			return Direction.SOUTH_WEST;
		case WEST:
			return Direction.WEST;
		case EAST:
			return Direction.EAST;
		case UP:
			return Direction.UP;
		case DOWN:
			return Direction.DOWN;
		default:
			return null;
		}
	}

	private void checkpoint(Asylum t) {
		try {
			db = new HandleDB();
			this.setCurrentRoom(t.getCurrentRoom());
			this.health=t.health;
			this.breathedGas=t.breathedGas;
			this.compassUsed=t.compassUsed;
			this.gasVuln=t.gasVuln;
			this.maxMoves=t.maxMoves;
			db.updateTuple(player, this);
			db.closeConnection();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private void changeRoom(CommandType c, PrintStream out) {
		ResourceBundle b = ResourceBundle.getBundle("function", lang);
		if(compassUsed || c==CommandType.UP || c==CommandType.DOWN) {
			try {
				Room next = searchDirection(commandToDirection(c));
				if (next == null) {
					out.println(b.getString("adjacent_direction_room"));
				} else if(getMap().readArc(getCurrentRoom(), next).isLocked()) {
					out.println(b.getString("door_blocked"));
				} else {
					setCurrentRoom(next);
					setCurrentEnemy(null);
					for(AdventureCharacter a: getCurrentRoom().getEnemies()) {
						if(a instanceof Enemy && a.getHealth()>0) {
							setCurrentEnemy((Enemy)a);
							break;
						}
					}
					//gestione trappola
					if(getCurrentRoom().hasTrap()) {
						getCurrentRoom().getTrap().accept(this);
					}
					out.println(getCurrentRoom().getDescription());
				}
			} catch (Exception e) {
				out.println(e.getMessage());
			}
		}else {
			out.println(b.getString("without_compass"));
		}
	}

	@Override
	public void nextMove(ParserOutput p, PrintStream out) {
		// TODO Auto-generated method stub
		ResourceBundle b = ResourceBundle.getBundle("function", lang);

		if (p.getObject()==null && p.getEnemy()==null && p.getTarget()==null) {
			switch (p.getCommand().getType()) {
			case INVENTORY:
				for(Item i : getInventory().getList()) {
					out.println(i.getName());
				}
				if(getInventory().getList().isEmpty())
					out.println(b.getString("empty_inv"));
				break;
			case LOOK_AT:
				out.println(getCurrentRoom().getLook());
				if(!getCurrentRoom().getObjects().isEmpty()) {
					if(getCurrentRoom().hasLight()) {
						out.println(b.getString("room_contain"));
						for(Item i : getCurrentRoom().getObjects()) {
							out.println(i.getName());
						}
					}
				}
				break;
			case NORD:
				changeRoom(p.getCommand().getType(), out);
				break;
			case NORTH_EAST:
				changeRoom(p.getCommand().getType(), out);
				break;
			case NORTH_WEST:
				changeRoom(p.getCommand().getType(), out);
				break;
			case SOUTH_EAST:
				changeRoom(p.getCommand().getType(), out);
				break;
			case SOUTH_WEST:
				changeRoom(p.getCommand().getType(), out);
				break;
			case EAST:
				changeRoom(p.getCommand().getType(), out);
				break;
			case WEST:
				changeRoom(p.getCommand().getType(), out);
				break;
			case SOUTH:
				changeRoom(p.getCommand().getType(), out);
				break;
			case UP:
				changeRoom(p.getCommand().getType(), out);
				break;
			case DOWN:
				changeRoom(p.getCommand().getType(), out);
				break;
			case WALK_TO:
				outer:
				try {
					for(Room a : getMap().getAdjacents(getCurrentRoom())) {
						if(a.getName().equals(p.getNextRoom())) {
							 if(getMap().readArc(getCurrentRoom(), a).isLocked()) {
								out.println(b.getString("door_blocked"));
								break outer;
							} else {
							setCurrentRoom(a);
							setCurrentEnemy(null);
							for(AdventureCharacter ad: getCurrentRoom().getEnemies()) {
								if(ad instanceof Enemy && ad.getHealth()>0) {
									setCurrentEnemy((Enemy)ad);
									break;
								}
							}
							//gestione trappola
							if(getCurrentRoom().hasTrap()) {
								getCurrentRoom().getTrap().accept(this);
							}
							if(this.health>0) {
								out.println(getCurrentRoom().getDescription());
							}
							break;
						}
					}
					}
					if(!getCurrentRoom().getName().equals(p.getNextRoom())) {
						out.println(b.getString("adjacent_name_room"));
					}

				}catch (Exception e) {
					// TODO: handle exception
				}
				break;
			default:
				out.println(ResourceBundle.getBundle("InvalidCommandException", lang).getString("standard"));

			}
		} else if(p.getObject()!=null && p.getTarget()==null) {
			p.getObject().getHandler().apply(p.getCommand().getType()).accept(this);
		} else if(p.getEnemy()!=null && p.getTarget()==null) {
			switch (p.getCommand().getType()) {
			case TALK_TO:
				if (p.getEnemy().getHealth()>0 && p.getEnemy() instanceof Enemy) {
				out.println(p.getEnemy().getTalk());
				}
				else {
					out.println(b.getString("enemy_killed"));
				}
				break;
			case BREAK:
				if(p.getEnemy().getHealth()>0 && p.getEnemy() instanceof Enemy) {
					setCurrentEnemy((Enemy)p.getEnemy());
					p.getEnemy().setHealth(p.getEnemy().getHealth()-5);
				}else {
					out.println(b.getString("speak_dead"));
				}
				break;
			case LOOK_AT:
				out.println(p.getEnemy().getDescription());
				if(p.getEnemy().getHealth() <= 0) {
					for(Item i: p.getEnemy().getInv().getList()) {
						getCurrentRoom().getObjects().add(i);
					}
					p.getEnemy().getInv().getList().clear();
				}
				break;
			default:
				out.println(ResourceBundle.getBundle("InvalidCommandException", lang).getString("standard"));
				break;
			}
		} else if(p.getObject()!=null && p.getTarget()!=null) {
			switch (p.getCommand().getType()) {
			case DROP:
				if(p.getTarget() instanceof ItemContainer) {
					p.getObject().getHandler().apply(CommandType.DROP);
					getCurrentRoom().getObjects().remove(p.getObject());
					((ItemContainer) p.getTarget()).add(p.getObject());
				}else {
					out.println(ResourceBundle.getBundle("InvalidCommandException", lang).getString("standard"));
				}
				break;
			case BREAK:
				if(p.getTarget() instanceof Weapon) {
					p.getObject().getHandler().apply(CommandType.BREAK);
					Integer s = ((Weapon) p.getTarget()).getShots();
					((Weapon) p.getTarget()).setShots(s-1);
				}else {
					out.println(ResourceBundle.getBundle("InvalidCommandException", lang).getString("standard"));
				}
				break;
			default:
				out.println(ResourceBundle.getBundle("InvalidCommandException", lang).getString("standard"));
				break;
			}
		}

		//gestione del gas
		if(this.breathedGas) {
			switch(maxMoves) {
				case 4:
					out.println(b.getString("gas_moves_1"));
					break;
				case 3:
					out.println(b.getString("gas_moves_2"));
					break;
				case 2:
					out.println(b.getString("gas_moves_3"));
					break;
				case 1:
					out.println(b.getString("gas_moves_4"));
					break;
				case 0:
					out.println(b.getString("died_gas"));
					health = 0;
					break;
				}
				maxMoves--;
			}

		if(getCurrentEnemy()!=null && getCurrentEnemy().getHealth()>0 && getCurrentRoom().hasLight()) {
			health = health - getCurrentEnemy().getDamage();
			if(health<0) health=0;
			out.println(getCurrentEnemy().getName()+" "+b.getString("attacked")+" "+health);
			out.println(getCurrentEnemy().getName()+" "+b.getString("has_health")+" "+getCurrentEnemy().getHealth());
		}
		if(getCurrentEnemy()!=null && getCurrentEnemy().getHealth()<=0) {
			out.println(b.getString("killed")+" "+getCurrentEnemy().getName()+"!");
			if(getCurrentEnemy().getDroppable()!=null) {
				getCurrentRoom().getObjects().add(getCurrentEnemy().getDroppable());
				out.println(getCurrentEnemy().getName()+" "+b.getString("released")+" "+getCurrentEnemy().getDroppable().getName());
				getCurrentEnemy().setDroppable(null);
			}
			setCurrentEnemy(null);
		}

		if(health==0) {
			out.println(b.getString("game_over"));
			Thread.currentThread().interrupt();
			return;
		}

	}

	private void loadLocales() {

		//consumer used for bfs
		Consumer<Room> c = new Consumer<Room>() {

			@Override
			public void accept(Room t) {
				// TODO Auto-generated method stub
				//loading bundles for room, items, trap and adventurecharacter
				ResourceBundle r = ResourceBundle.getBundle("room", Manager.locale);
				ResourceBundle i = ResourceBundle.getBundle("item", Manager.locale);
				ResourceBundle tr = ResourceBundle.getBundle("trap", Manager.locale);
				ResourceBundle e = ResourceBundle.getBundle("enemy", Manager.locale);
				//edit room info
				t.setDescription(r.getString("descr_room_".concat((t.getId()).toString())));
				t.setLook(r.getString("look_room_".concat(t.getId().toString())));
				t.setName(r.getString("name_room_".concat(t.getId().toString())));
				//edit items info of current room
				for(Item it : t.getObjects()) {
					it.setName(i.getString("name_item_".concat(it.getId().toString())));
					it.setDescription(i.getString("descr_item_".concat(it.getId().toString())));
					//check for containers ("recursive")
					if(it instanceof ItemContainer) {
						for(Item k : ((ItemContainer) it).getContent()) {
							k.setName(i.getString("name_item_".concat(k.getId().toString())));
							k.setDescription(i.getString("descr_item_".concat(k.getId().toString())));
						}
					}
				}
				//edit character info
				for(AdventureCharacter a : t.getEnemies()) {
					a.setDescription(e.getString("descr_enemy_".concat(a.getId().toString())));
					a.setName(e.getString("name_enemy_".concat(a.getId().toString())));
					a.setTalk(e.getString("descr_enemy_".concat(a.getId().toString())));
					//edit character inventory
					for(Item it: a.getInv().getList()) {
						it.setName(i.getString("name_item_".concat(it.getId().toString())));
						it.setDescription(i.getString("descr_item_".concat(it.getId().toString())));
					}
					//check for droppable
					if(a.getDroppable()!=null) {
						Item it = a.getDroppable();
						a.getDroppable().setName(i.getString("name_item_".concat(it.getId().toString())));
						a.getDroppable().setDescription(i.getString("descr_item_".concat(it.getId().toString())));
					}
				}

			}

		};
		try {
			//perform BFS with the above declared consumer starting from the current room
			this.getMap().BFS(c, getCurrentRoom());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		//edit info on your inventory
		ResourceBundle i = ResourceBundle.getBundle("item", Manager.locale);
		for(Item it: getInventory().getList()) {
			it.setName(i.getString("name_item_".concat(it.getId().toString())));
			it.setDescription(i.getString("descr_item_".concat(it.getId().toString())));
			//if compass used, update look_at of all rooms
			if(it.getName().equals("compass") || it.getName().equals("bussola") && this.compassUsed) {
				it.getHandler().apply(CommandType.USE).accept(this);
			}
			//if gasmask used, update look_at of regarding rooms
			if(it.getName().equals("gas-mask") || it.getName().equals("maschera-gas") && !this.gasVuln) {
				it.getHandler().apply(CommandType.USE).accept(this);
			}
		}

		List<Command> newCommands = new ArrayList<Command>();
		ResourceBundle commBundle = ResourceBundle.getBundle("command", Manager.locale);
		for(Command com : this.getCommands()) {
			Command nc = new Command(com.getType(), commBundle.getString("name_command_"+com.getType().name().toLowerCase()));
			nc.setAlias(commBundle.getString("alias_command_"+com.getType().name().toLowerCase()).split(" "));
			newCommands.add(nc);
		}
		this.setCommands(newCommands);

	}



}
