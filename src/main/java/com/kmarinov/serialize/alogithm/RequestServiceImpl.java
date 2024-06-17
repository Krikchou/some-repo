package com.kmarinov.serialize.alogithm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kmarinov.serialize.config.RequestToMethodBindingBean;
import com.kmarinov.serialize.dao.RequestRepository;
import com.kmarinov.serialize.entities.Request;
import com.kmarinov.serialize.enums.RequestStateEnum;

@Service
public class RequestServiceImpl implements RequestService {
	
	@Autowired private RequestToMethodBindingBean mappings;
	
	@Autowired private RequestRepository requestDao;

	@Override
	public Request create(String serviceRelated, String body) {
		Request req = new Request();
		req.setRequestBody(body);
		req.setServiceRelated(serviceRelated);
		req.setApiPath(mappings.getPathByMethodName(serviceRelated));
		req.setState(RequestStateEnum.UNKNKOWN);
		
		return requestDao.save(req);
	}

	@Override
	public Request create(String serviceRelated, String body, String apiPath) {
		Request req = new Request();
		req.setRequestBody(body);
		req.setServiceRelated(serviceRelated);
		req.setApiPath(apiPath);
		req.setState(RequestStateEnum.UNKNKOWN);
		
		return requestDao.save(req);
	}

}
