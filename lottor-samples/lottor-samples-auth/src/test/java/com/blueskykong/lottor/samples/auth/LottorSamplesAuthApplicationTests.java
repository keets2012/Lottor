package com.blueskykong.lottor.samples.auth;

import com.blueskykong.lottor.samples.auth.domain.Permission;
import com.blueskykong.lottor.samples.auth.service.mapper.PermissionMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
@EnableDiscoveryClient
public class LottorSamplesAuthApplicationTests {

	@Autowired
	PermissionMapper permissionMapper;

	@Test
	public void contextLoads() {
	}

	@Test
	public void createPermission() {
		permissionMapper.savePermission(new Permission(UUID.randomUUID().toString(),"ALL" ,"所有权限" ));

		permissionMapper.savePermission(new Permission(UUID.randomUUID().toString(),"READ_ONLY" ,"只读权限" ));

	}

}
