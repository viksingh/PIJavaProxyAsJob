package org.job;

import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.xml.ws.WebServiceRef;

import com.sap.engine.services.webservices.espbase.client.api.XIManagementInterface;
import com.sap.engine.services.webservices.espbase.client.api.XIManagementInterfaceFactory;
import com.sap.engine.services.webservices.espbase.client.api.XIMessageContext;
import com.sap.scheduler.runtime.JobContext;
import com.sap.scheduler.runtime.mdb.MDBJobImplementation;
import com.sap.xi.xi.demo.airline.BookingID;
import com.sap.xi.xi.demo.airline.FlightBookingOrderConfirmation;
import com.sap.xi.xi.demo.airline.FlightBookingOrderConfirmationOut;
import com.sap.xi.xi.demo.airline.FlightBookingOrderConfirmationOutService;

/**
 * Message-Driven Bean implementation class for: SendProxyAsync
 *
 */
@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "messageSelector", propertyValue = "JobDefinition=\'SendProxyAsync\' AND ApplicationName=\'sap.com/FlightOutEAR\'") })
		public class SendProxyAsync extends MDBJobImplementation {

	@WebServiceRef(name = "FlightBookingOrderConfirmationOutService")	
	private  FlightBookingOrderConfirmationOutService service;

	/**
	 * @see MDBJobImplementation#MDBJobImplementation()
	 */
	public SendProxyAsync() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onJob(JobContext jobContext) throws Exception {

		Logger log = jobContext.getLogger();
		try{
		

		log.info("STARTING JOB ");

		FlightBookingOrderConfirmationOut port = service
		.getPort(FlightBookingOrderConfirmationOut.class);

		log.info("STAGE 1 - Create XI MGMT Objects");

		XIManagementInterface xiMngmnt = XIManagementInterfaceFactory
		.create(port);
		XIMessageContext msgCtx = xiMngmnt.getRequestXIMessageContext();

		log.info("STAGE 2 - Add scenario specific values");
		msgCtx.setSenderPartyName("");
		msgCtx.setSenderService("BS_SENDER");

		log.info("STAGE 3 - Update business data");
		FlightBookingOrderConfirmation flightBookingOrderConfirmation = new FlightBookingOrderConfirmation();

		BookingID id = new BookingID();
		id.setAirlineID("SG");
		id.setBookingNumber("452");

		log.info("STAGE 4 - Try Send");
		flightBookingOrderConfirmation.setBookingID(id);

		port.flightBookingOrderConfirmationOut(flightBookingOrderConfirmation);

		log.info("JOB COMPLETED !");
		
		}catch( Exception e ){
			
			log.info(e.toString());
		}



	}

}
