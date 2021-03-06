package Test;
import Paxos.*;
import java.util.LinkedList;
import java.util.Stack;
public class TestNetwork extends Network {
  int test_totalProcesses;
  int test_numProposers;
  int test_numAcceptors;
  int test_numLearners;
  int test_decision=-1;
  int error_flag=0;// 1 -> Print Message Trace
  TestChannel channels[]=new TestChannel[100];
  LinkedList<String>[] test_queues;
  Stack<String>[] test_stacks;
  LinkedList<String> MessageTrace;

  /** Create a network with test_numProposers proposes, test_numAcceptors
   * acceptors, and test_numLearners learners.*/

  @SuppressWarnings("unchecked")
  public TestNetwork(int numProposers, int numAcceptors, int numLearners) {
    super(numProposers,numAcceptors,numLearners);
    test_totalProcesses=numProposers+numAcceptors+numLearners;
    test_queues=new LinkedList[test_totalProcesses];
    test_stacks=new Stack[test_totalProcesses];
    MessageTrace=new LinkedList();
    for(int i=0;i<test_totalProcesses;i++) {
      test_queues[i]=new LinkedList<String>();
      test_stacks[i]=new Stack<String>();
    }
    this.test_numProposers=numProposers;
    this.test_numAcceptors=numAcceptors;
    this.test_numLearners=numLearners;
    channels=new TestChannel[test_totalProcesses];
  }
  
  public int numAcceptors() {
    return test_numAcceptors;
  }

  public int numProposers() {
    return test_numProposers;
  }

  public int numLearners() {
    return test_numLearners;
  }

  /** getChannel returns a communication channel for process processID.
   *
   *   Process ids:
   *   0 through test_numProposers-1 should be Proposers
   *
   *   test_numProposers through numAccepters+numProposes-1 should be Acceptors
   *
   *   numAccepters+numProposes through
   *   numAccepters+numProposes+test_numLearners-1 should be Learners */

  public TestChannel getChannel(int processID) {
    if (processID<0 || processID>= test_totalProcesses) {
      throw new Error("Invalid process ID.");
    }
    TestChannel c=new TestChannel();
    c.test_index=processID;
    channels[processID]=c; 
    c.test_network=this;
    return c;
  
  }
  public void block_channel(int PID,int action){ //action 0->release, 1->block
    while(channels[PID]==null){} //Added to all testhelper function to inifinitely wait until the channel is initialised rather than providing explicit sleeps
    channels[PID].block_channel=action;   
  }

  public void terminate_run(){
    for(int i=0;i<test_totalProcesses;i++){
      while(channels[i]==null){}
        channels[i].terminate=1;
    }
  }
  
  public void change_DPmode(int PID,int mode){
    for(int i=0;i<test_numProposers;i++){
      while(channels[i]==null){}
      channels[i].DP_mode=mode;
      channels[i].requested_DP=PID; 
    }
  }


  public void lossy_channel(int PID,int flag){
    if(flag==1) 
      channels[PID].update_MessageTrace(" LOSSY CHANNEL ENABLED, Cannot receive any more messages");
    if(flag==0) 
      channels[PID].update_MessageTrace(" LOSSY CHANNEL DISABLED, Resume receiving messages");
    while(channels[PID]==null){}
    channels[PID].lose_msg=flag;
  }
  public void dup_msg(int PID, int flag) {
    while(channels[PID]==null){}
    channels[PID].dup_msg = flag;
  }
  public void reorder_msg(int PID,int flag){
    while(channels[PID]==null){}
    channels[PID].reorder_msg = flag;
  }
  public void shuffle_msg(int PID){
    channels[PID].update_MessageTrace(" RANDOMLY SHUFFLE ALL MESSAGES in order to RE-ORDER them");
    while(channels[PID]==null){}
    channels[PID].shuffle_msg();
  }
  public void change_init_logic(int logic){
    for(int PID=0;PID<test_totalProcesses;PID++){
     while(channels[PID]==null){}
     channels[PID].init_logic=logic;
    }
  }

  public void printTrace(){
    if(error_flag==1){
      while(MessageTrace.size()!=0){
        System.out.println(MessageTrace.remove());
      } 
    }
  }
}

